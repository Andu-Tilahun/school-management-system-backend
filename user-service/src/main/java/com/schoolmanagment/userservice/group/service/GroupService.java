package com.schoolmanagment.userservice.group.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.userservice.group.dto.GroupDto;
import com.schoolmanagment.userservice.group.dto.GroupRequest;
import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.group.mapper.GroupMapper;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.group.repository.GroupRepository;
import com.schoolmanagment.userservice.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final PolicyRepository policyRepository;
    private final GroupMapper groupMapper;

    @Transactional
    public GroupDto create(GroupRequest request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new BadRequestException("Group name already exists");
        }

        Set<Policy> policies = resolvePolicies(request.getPolicyIds());
        Group group = groupMapper.toEntity(request, policies);
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public Page<GroupDto> getAll(Pageable pageable) {
        return groupRepository.findAll(pageable).map(groupMapper::toDto);
    }

    @Transactional(readOnly = true)
    public GroupDto getById(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        return groupMapper.toDto(group);
    }

    @Transactional
    public GroupDto update(UUID id, GroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));

        if (!group.getName().equals(request.getName()) && groupRepository.existsByName(request.getName())) {
            throw new BadRequestException("Group name already exists");
        }

        Set<Policy> policies = resolvePolicies(request.getPolicyIds());
        groupMapper.updateEntity(group, request, policies);
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Transactional
    public void delete(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        groupRepository.delete(group);
    }

    private Set<Policy> resolvePolicies(Set<UUID> policyIds) {
        Set<Policy> policies = new HashSet<>();
        if (policyIds == null) {
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
