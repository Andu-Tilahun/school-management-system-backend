package com.schoolmanagment.userservice.scope.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.userservice.scope.dto.ScopeDto;
import com.schoolmanagment.userservice.scope.dto.ScopeRequest;
import com.schoolmanagment.userservice.scope.entity.Scope;
import com.schoolmanagment.userservice.scope.mapper.ScopeMapper;
import com.schoolmanagment.userservice.scope.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;
    private final ScopeMapper scopeMapper;

    @Transactional
    public ScopeDto create(ScopeRequest request) {
        if (scopeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Scope name already exists");
        }

        Scope scope = scopeMapper.toEntity(request);
        return scopeMapper.toDto(scopeRepository.save(scope));
    }

    @Transactional(readOnly = true)
    public Page<ScopeDto> getAll(Pageable pageable) {
        return scopeRepository.findAll(pageable).map(scopeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ScopeDto getById(UUID id) {
        Scope scope = scopeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scope not found with id: " + id));
        return scopeMapper.toDto(scope);
    }

    @Transactional
    public ScopeDto update(UUID id, ScopeRequest request) {
        Scope scope = scopeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scope not found with id: " + id));

        if (!scope.getName().equals(request.getName()) && scopeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Scope name already exists");
        }

        scopeMapper.updateEntity(scope, request);
        return scopeMapper.toDto(scopeRepository.save(scope));
    }

    @Transactional
    public void delete(UUID id) {
        Scope scope = scopeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scope not found with id: " + id));
        scopeRepository.delete(scope);
    }
}
