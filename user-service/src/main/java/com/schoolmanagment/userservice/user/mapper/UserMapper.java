package com.schoolmanagment.userservice.user.mapper;

import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.group.mapper.GroupMapper;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.policy.mapper.PolicyMapper;
import com.schoolmanagment.userservice.user.dto.UserDto;
import com.schoolmanagment.userservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final GroupMapper groupMapper;
    private final PolicyMapper policyMapper;

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .gender(user.getGender())
                .profileImageUuid(user.getProfileImageUuid())
                .enabled(user.getEnabled())
                .accountNonLocked(user.getAccountNonLocked())
                .groups(user.getGroups() == null ? Set.of() : user.getGroups().stream()
                        .map(groupMapper::toSummaryDto)
                        .collect(Collectors.toSet()))
                .directPolicies(user.getPolicies() == null ? Set.of() : user.getPolicies().stream()
                        .map(policyMapper::toSummaryDto)
                        .collect(Collectors.toSet()))
                .effectivePolicyIds(
                        Stream.concat(
                                user.getPolicies() != null ? user.getPolicies().stream() : Stream.empty(),
                                user.getGroups() != null
                                        ? user.getGroups().stream()
                                        .flatMap(group -> group.getPolicies() != null
                                                ? group.getPolicies().stream()
                                                : Stream.empty())
                                        : Stream.empty()
                        )
                                .map(Policy::getId)
                                .collect(Collectors.toSet()))
                .effectivePolicyNames(collectEffectivePolicyNames(user))
                .userScopeType(user.getUserScopeType())
                .externalId(user.getExternalId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private static Set<String> collectEffectivePolicyNames(User user) {
        Stream<Policy> fromUser =
                user.getPolicies() != null ? user.getPolicies().stream() : Stream.empty();
        Stream<Policy> fromGroups =
                user.getGroups() != null
                        ? user.getGroups().stream()
                        .flatMap(g -> g.getPolicies() != null ? g.getPolicies().stream() : Stream.empty())
                        : Stream.empty();
        Set<String> names = new HashSet<>();
        Stream.concat(fromUser, fromGroups).forEach(p -> names.add(p.getName()));
        return names;
    }
}
