package com.schoolmanagment.userservice.user.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserStatusCache;
import com.schoolmanagment.userservice.user.dto.UserRegisterRequest;
import com.schoolmanagment.userservice.user.dto.UserUpdateRequest;
import com.schoolmanagment.userservice.user.dto.UserDto;
import com.schoolmanagment.userservice.user.dto.UserFilterRequest;
import com.schoolmanagment.userservice.user.enums.UserScopeType;
import com.schoolmanagment.userservice.user.mapper.UserMapper;
import com.schoolmanagment.userservice.user.entity.PasswordResetToken;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.user.entity.User;
import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.kafka.NotifierEventProducer;
import com.schoolmanagment.userservice.group.repository.GroupRepository;
import com.schoolmanagment.userservice.user.repository.PasswordResetTokenRepository;
import com.schoolmanagment.userservice.policy.repository.PolicyRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String ADMIN_ALL_FEATURES_POLICY = "ADMIN_ALL_FEATURES";

    /** Must match seeded group in V12__alter_user.sql */
    private static final String ADMIN_DEFAULT_NAVIGATION_GROUP = "ADMIN_FULL_ACCESS";

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
        addDefaultNavigationGroupForFullAccess(groups, policies);

        validateUserScope(request.getUserScopeType(), request.getExternalId());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .profileImageUuid(request.getProfileImageUuid())
                .userScopeType(request.getUserScopeType())
                .externalId(request.getUserScopeType() == UserScopeType.SYSTEM ? null : request.getExternalId())
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
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findWithPoliciesGraphByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

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
        if (request.getUserScopeType() != null) {
            validateUserScope(request.getUserScopeType(), request.getExternalId());
            user.setUserScopeType(request.getUserScopeType());
            user.setExternalId(request.getUserScopeType() == UserScopeType.SYSTEM ? null : request.getExternalId());
        }
        if (request.getGroupIds() != null) {
            user.setGroups(new HashSet<>(resolveGroups(request.getGroupIds())));
        }
        if (request.getPolicyIds() != null) {
            user.setPolicies(resolvePolicies(request.getPolicyIds()));
        }
        if (request.getGroupIds() != null || request.getPolicyIds() != null) {
            Set<Group> mergedGroups = new HashSet<>(user.getGroups() != null ? user.getGroups() : Set.of());
            Set<Policy> mergedPolicies = new HashSet<>(user.getPolicies() != null ? user.getPolicies() : Set.of());
            addDefaultNavigationGroupForFullAccess(mergedGroups, mergedPolicies);
            user.setGroups(mergedGroups);
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (hasEffectivePolicy(user, ADMIN_ALL_FEATURES_POLICY)) {
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

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


    private void addDefaultNavigationGroupForFullAccess(Set<Group> groups, Set<Policy> directPolicies) {
        boolean qualifies = groups.stream().anyMatch(g -> ADMIN_DEFAULT_NAVIGATION_GROUP.equals(g.getName()))
                || (directPolicies != null && directPolicies.stream()
                .anyMatch(p -> ADMIN_ALL_FEATURES_POLICY.equals(p.getName())));
        if (!qualifies) {
            return;
        }
        groupRepository.findByName(ADMIN_DEFAULT_NAVIGATION_GROUP).ifPresent(navGroup -> {
            boolean alreadyPresent = groups.stream().anyMatch(g -> g.getId().equals(navGroup.getId()));
            if (!alreadyPresent) {
                groups.add(navGroup);
            }
        });
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

    private static void validateUserScope(UserScopeType type, UUID externalId) {
        if (type == null) {
            throw new BadRequestException("User type is required");
        }
        if (type == UserScopeType.SYSTEM) {
            return;
        }
        if (externalId == null) {
            throw new BadRequestException("A region or clearing agent organization must be selected for this user type");
        }
    }
}
