package com.schoolmanagment.userservice.permission.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.userservice.permission.dto.PermissionDto;
import com.schoolmanagment.userservice.permission.dto.PermissionFilterRequest;
import com.schoolmanagment.userservice.permission.dto.PermissionRequest;
import com.schoolmanagment.userservice.permission.entity.Permission;
import com.schoolmanagment.userservice.permission.mapper.PermissionMapper;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.resource.entity.Resource;
import com.schoolmanagment.userservice.scope.entity.Scope;
import com.schoolmanagment.userservice.permission.repository.PermissionRepository;
import com.schoolmanagment.userservice.policy.repository.PolicyRepository;
import com.schoolmanagment.userservice.resource.repository.ResourceRepository;
import com.schoolmanagment.userservice.scope.repository.ScopeRepository;
import com.schoolmanagment.userservice.permission.specification.PermissionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ScopeRepository scopeRepository;
    private final ResourceRepository resourceRepository;
    private final PolicyRepository policyRepository;
    private final PermissionMapper permissionMapper;

    @Transactional
    public PermissionDto create(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new BadRequestException("Permission name already exists");
        }

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + request.getResourceId()));

        Set<Scope> scopes = resolveScopes(request.getScopeIds());

        Permission permission = permissionMapper.toEntity(request, resource, scopes);

        permission = permissionRepository.save(permission);
        syncLinkedPolicies(permission, request.getPolicyIds());

        return permissionMapper.toDto(permissionRepository.findById(permission.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public Page<PermissionDto> filterPermissions(PermissionFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return permissionRepository
                .findAll(new PermissionSpecification(request), pageable)
                .map(permissionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PermissionDto getById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return permissionMapper.toDto(permission);
    }

    @Transactional
    public PermissionDto update(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        if (!permission.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new BadRequestException("Permission name already exists");
        }

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + request.getResourceId()));

        Set<Scope> scopes = resolveScopes(request.getScopeIds());

        permissionMapper.updateEntity(permission, request, resource, scopes);

        syncLinkedPolicies(permission, request.getPolicyIds());

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Transactional
    public void delete(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        permissionRepository.delete(permission);
    }

    private Set<Scope> resolveScopes(Set<UUID> scopeIds) {
        Set<Scope> scopes = new HashSet<>();
        if (scopeIds == null || scopeIds.isEmpty()) {
            return scopes;
        }
        for (UUID scopeId : scopeIds) {
            Scope scope = scopeRepository.findById(scopeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Scope not found with id: " + scopeId));
            scopes.add(scope);
        }
        return scopes;
    }

    /**
     * Policy owns the permission_policies join; keep both sides consistent.
     */
    private void syncLinkedPolicies(Permission permission, Set<UUID> policyIds) {
        if (permission.getPolicies() != null) {
            for (Policy policy : new HashSet<>(permission.getPolicies())) {
                policy.getPermissions().remove(permission);
            }
        }
        if (policyIds == null || policyIds.isEmpty()) {
            return;
        }
        for (UUID policyId : policyIds) {
            Policy policy = policyRepository.findById(policyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + policyId));
            policy.getPermissions().add(permission);
        }
    }
}
