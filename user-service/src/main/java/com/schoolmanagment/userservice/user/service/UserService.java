package com.schoolmanagment.userservice.user.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.commonsecurity.util.UserStatusCache;
import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.group.repository.GroupRepository;
import com.schoolmanagment.userservice.kafka.NotifierEventProducer;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.policy.repository.PolicyRepository;
import com.schoolmanagment.userservice.user.dto.UserDto;
import com.schoolmanagment.userservice.user.dto.UserFilterRequest;
import com.schoolmanagment.userservice.user.dto.UserRegisterRequest;
import com.schoolmanagment.userservice.user.dto.UserUpdateRequest;
import com.schoolmanagment.userservice.user.entity.PasswordResetToken;
import com.schoolmanagment.userservice.user.entity.User;
import com.schoolmanagment.userservice.user.mapper.UserMapper;
import com.schoolmanagment.userservice.user.repository.PasswordResetTokenRepository;
import com.schoolmanagment.userservice.user.repository.UserRepository;
import com.schoolmanagment.userservice.user.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PolicyRepository policyRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotifierEventProducer notifierEventProducer;

    private final UserStatusCache userStatusCache;
    private final UserMapper userMapper;

    @Transactional
    public UserDto registerUser(UserRegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Set<Group> groups = new HashSet<>(resolveGroups(request.getGroupIds()));
        Set<Policy> policies = resolvePolicies(request.getPolicyIds());
        UUID externalId = applyHierarchyAndGroups(groups, policies, request.getExternalId(), null);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .profileImageUuid(request.getProfileImageUuid())
                .externalId(externalId)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .groups(groups)
                .policies(policies)
                .build();

        User savedUser = userRepository.save(user);

        notifierEventProducer.sendWelcomeMessageByEmail(savedUser);

        return userMapper.toDto(savedUser);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        Specification<User> specification = new UserSpecification(new UserFilterRequest());
        return userRepository.findAll(specification, pageable).map(userMapper::toDto);
    }

    public UserDto getUserById(UUID id) {
        return userMapper.toDto(findUserInScope(id));
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findWithPoliciesGraphByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserUpdateRequest request) {
        User user = findUserInScope(id);

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setGender(request.getGender());
        user.setProfileImageUuid(request.getProfileImageUuid());
        if (request.getGroupIds() != null) {
            user.setGroups(new HashSet<>(resolveGroups(request.getGroupIds())));
        }
        if (request.getPolicyIds() != null) {
            user.setPolicies(resolvePolicies(request.getPolicyIds()));
        }
        Set<Group> mergedGroups = new HashSet<>(user.getGroups() != null ? user.getGroups() : Set.of());
        Set<Policy> mergedPolicies = new HashSet<>(user.getPolicies() != null ? user.getPolicies() : Set.of());
        user.setExternalId(applyHierarchyAndGroups(mergedGroups, mergedPolicies, request.getExternalId(), user.getId()));
        user.setGroups(mergedGroups);
        user.setPolicies(mergedPolicies);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = findUserInScope(id);
        userRepository.delete(user);
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!user.getAccountNonLocked()) {
            throw new BadRequestException("Your account is locked.Please contact the system administrator.");
        }

        // Delete existing tokens for this user
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(resetToken);

        notifierEventProducer.sendPasswordResetMessageByEmail(user, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token after successful password reset
        tokenRepository.delete(resetToken);

        notifierEventProducer.sendPasswordChangedMessageByEmail(user);
    }

    @Transactional
    public UserDto lockUser(UUID id) {
        User user = findUserInScope(id);

        if (hasEffectivePolicy(user, PolicyNames.SUPER_ADMIN_FEATURES)) {
            throw new BadRequestException("Cannot lock admin users");
        }

        user.setAccountNonLocked(false);
        User updatedUser = userRepository.save(user);

        userStatusCache.addInactiveUser(id.toString());

        notifierEventProducer.sendAccountLockedByEmail(user);

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto unlockUser(UUID id) {
        User user = findUserInScope(id);

        user.setAccountNonLocked(true);
        User updatedUser = userRepository.save(user);

        userStatusCache.removeUser(id.toString());

        notifierEventProducer.sendAccountUnlockedByEmail(user);

        return userMapper.toDto(updatedUser);
    }

    public Page<UserDto> filterUsers(UserFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Specification<User> specification = new UserSpecification(request);
        return userRepository.findAll(specification, pageable).map(userMapper::toDto);
    }


    private User findUserInScope(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        assertUserInScope(user);
        return user;
    }

    private void assertUserInScope(User user) {
        UserContext ctx = UserContext.current();
        if (ctx == null || !ctx.isAuthenticated() || ctx.hasAdminPolicy()) {
            return;
        }
        if (user.getId() != null && user.getId().equals(ctx.getCurrentUserId())) {
            return;
        }
        if (ctx.hasSchoolAdminPolicy()) {
            UUID schoolId = ctx.getCurrentExternalId().orElse(null);
            if (schoolId == null || !schoolId.equals(user.getExternalId())) {
                throw new ResourceNotFoundException("User not found with id: " + user.getId());
            }
            return;
        }
        if (ctx.hasTenantManager()) {
            if (hasEffectivePolicy(user, PolicyNames.SUPER_ADMIN_FEATURES)
                    || hasEffectivePolicy(user, PolicyNames.TENANT_MANAGER_POLICY)) {
                throw new ResourceNotFoundException("User not found with id: " + user.getId());
            }
        }
    }

    private UUID applyHierarchyAndGroups(
            Set<Group> groups,
            Set<Policy> policies,
            UUID requestedExternalId,
            UUID targetUserId
    ) {
        UserContext ctx = UserContext.current();
        boolean authenticated = ctx != null && ctx.isAuthenticated();

        if (authenticated) {
            Set<String> assignedNames = collectAssignedPolicyNames(groups, policies);
            boolean selfUpdate = targetUserId != null && targetUserId.equals(ctx.getCurrentUserId());
            if (ctx.hasAdminPolicy()) {
                if (assignedNames.contains(PolicyNames.TENANT_MANAGER_POLICY) && requestedExternalId == null) {
                    throw new BadRequestException("Tenant Manager users must have externalId set to the tenant id");
                }
                if (assignedNames.contains(PolicyNames.SCHOOL_ADMIN_POLICY) && requestedExternalId == null) {
                    throw new BadRequestException("School Admin users must have externalId set to the school id");
                }
            } else if (ctx.hasTenantManager()) {
                if (selfUpdate) {
                    if (assignedNames.contains(PolicyNames.SUPER_ADMIN_FEATURES)) {
                        throw new BadRequestException("Cannot assign SUPER_ADMIN_FEATURES");
                    }
                    if (requestedExternalId == null) {
                        requestedExternalId = ctx.getCurrentExternalId().orElse(null);
                    }
                } else {
                    boolean onlySchoolAdmin = !assignedNames.isEmpty()
                            && assignedNames.stream().allMatch(PolicyNames.SCHOOL_ADMIN_POLICY::equals);
                    if (!onlySchoolAdmin) {
                        throw new BadRequestException("Tenant Manager may assign SCHOOL_ADMIN_POLICY only");
                    }
                    if (requestedExternalId == null) {
                        throw new BadRequestException("School Admin users must have externalId set to the school id");
                    }
                }
            } else if (ctx.hasSchoolAdminPolicy()) {
                if (assignedNames.contains(PolicyNames.SUPER_ADMIN_FEATURES)
                        || assignedNames.contains(PolicyNames.TENANT_MANAGER_POLICY)
                        || (!selfUpdate && assignedNames.contains(PolicyNames.SCHOOL_ADMIN_POLICY))) {
                    throw new BadRequestException("School Admin cannot assign admin policies");
                }
                requestedExternalId = ctx.getCurrentExternalId()
                        .orElseThrow(() -> new BadRequestException(
                                "School Admin users must have an externalId"));
            } else if (assignedNames.contains(PolicyNames.SUPER_ADMIN_FEATURES)
                    || assignedNames.contains(PolicyNames.TENANT_MANAGER_POLICY)
                    || assignedNames.contains(PolicyNames.SCHOOL_ADMIN_POLICY)) {
                throw new BadRequestException("Cannot assign admin policies");
            }
        }

        addMatchingPolicyGroups(groups, policies);
        return requestedExternalId;
    }

    private Set<String> collectAssignedPolicyNames(Set<Group> groups, Set<Policy> policies) {
        Set<String> names = new HashSet<>();
        if (policies != null) {
            for (Policy policy : policies) {
                names.add(policy.getName());
            }
        }
        if (groups != null) {
            for (Group group : groups) {
                names.add(group.getName());
                if (group.getPolicies() != null) {
                    for (Policy policy : group.getPolicies()) {
                        names.add(policy.getName());
                    }
                }
            }
        }
        return names;
    }

    private void addMatchingPolicyGroups(Set<Group> groups, Set<Policy> policies) {
        if (policies == null) {
            return;
        }
        for (Policy policy : policies) {
            groupRepository.findByName(policy.getName()).ifPresent(matchingGroup -> {
                boolean alreadyPresent = groups.stream().anyMatch(g -> g.getId().equals(matchingGroup.getId()));
                if (!alreadyPresent) {
                    groups.add(matchingGroup);
                }
            });
        }
    }

    private boolean hasEffectivePolicy(User user, String policyName) {
        if (user.getPolicies() != null) {
            for (Policy p : user.getPolicies()) {
                if (policyName.equals(p.getName())) {
                    return true;
                }
            }
        }
        if (user.getGroups() != null) {
            for (Group g : user.getGroups()) {
                if (g.getPolicies() != null) {
                    for (Policy p : g.getPolicies()) {
                        if (policyName.equals(p.getName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Set<Group> resolveGroups(Set<UUID> groupIds) {
        Set<Group> groups = new HashSet<>();
        if (groupIds == null || groupIds.isEmpty()) {
            return groups;
        }
        for (UUID groupId : groupIds) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
            groups.add(group);
        }
        return groups;
    }

    private Set<Policy> resolvePolicies(Set<UUID> policyIds) {
        Set<Policy> policies = new HashSet<>();
        if (policyIds == null || policyIds.isEmpty()) {
            return policies;
        }
        for (UUID policyId : policyIds) {
            Policy policy = policyRepository.findById(policyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + policyId));
            policies.add(policy);
        }
        return policies;
    }
}
