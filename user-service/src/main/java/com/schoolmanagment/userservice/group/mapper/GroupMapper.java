package com.schoolmanagment.userservice.group.mapper;

import com.schoolmanagment.userservice.group.dto.GroupDto;
import com.schoolmanagment.userservice.group.dto.GroupRequest;
import com.schoolmanagment.userservice.group.dto.GroupSummaryDto;
import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.policy.mapper.PolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupMapper {

    private final PolicyMapper policyMapper;

    public GroupDto toDto(Group group) {
        if (group == null) {
            return null;
        }
        return GroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .policies(group.getPolicies() == null
                        ? Collections.emptySet()
                        : group.getPolicies().stream()
                                .map(policyMapper::toSummaryDto)
                                .collect(Collectors.toSet()))
                .createdAt(group.getCreatedAt())
                .build();
    }

    public GroupSummaryDto toSummaryDto(Group group) {
        if (group == null) {
            return null;
        }
        return GroupSummaryDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .build();
    }

    public Group toEntity(GroupRequest request, Set<Policy> policies) {
        return Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .policies(policies != null ? new HashSet<>(policies) : new HashSet<>())
                .build();
    }

    public void updateEntity(Group group, GroupRequest request, Set<Policy> policies) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setPolicies(policies != null ? new HashSet<>(policies) : new HashSet<>());
    }
}
