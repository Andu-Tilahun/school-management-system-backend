package com.schoolmanagment.userservice.policy.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.userservice.policy.dto.PolicyDto;
import com.schoolmanagment.userservice.policy.dto.PolicyRequest;
import com.schoolmanagment.userservice.permission.entity.Permission;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.policy.mapper.PolicyMapper;
import com.schoolmanagment.userservice.permission.repository.PermissionRepository;
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
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PermissionRepository permissionRepository;
    private final PolicyMapper policyMapper;

    @Transactional
    public PolicyDto create(PolicyRequest request) {
        if (policyRepository.existsByName(request.getName())) {
            throw new BadRequestException("Policy name already exists");
        }

        Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
        Policy policy = policyMapper.toEntity(request, permissions);
        return policyMapper.toDto(policyRepository.save(policy));
    }

    @Transactional(readOnly = true)
    public Page<PolicyDto> getAll(Pageable pageable) {
        return policyRepository.findAll(pageable).map(policyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PolicyDto getById(UUID id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return policyMapper.toDto(policy);
    }

    @Transactional
    public PolicyDto update(UUID id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));

        if (!policy.getName().equals(request.getName()) && policyRepository.existsByName(request.getName())) {
            throw new BadRequestException("Policy name already exists");
        }

        Set<Permission> permissions = resolvePermissions(request.getPermissionIds());
        policyMapper.updateEntity(policy, request, permissions);
        return policyMapper.toDto(policyRepository.save(policy));
    }

    @Transactional
    public void delete(UUID id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        policyRepository.delete(policy);
    }

    private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
        Set<Permission> permissions = new HashSet<>();
        if (permissionIds == null) {
            return permissions;
        }

        for (UUID permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
            permissions.add(permission);
        }

        return permissions;
    }
}
