package com.schoolmanagment.userservice.resource.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.userservice.resource.dto.ResourceDto;
import com.schoolmanagment.userservice.resource.dto.ResourceRequest;
import com.schoolmanagment.userservice.resource.entity.Resource;
import com.schoolmanagment.userservice.resource.mapper.ResourceMapper;
import com.schoolmanagment.userservice.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto create(ResourceRequest request) {
        if (resourceRepository.existsByName(request.getName())) {
            throw new BadRequestException("Resource name already exists");
        }

        Resource resource = resourceMapper.toEntity(request);
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    @Transactional(readOnly = true)
    public Page<ResourceDto> getAll(Pageable pageable) {
        return resourceRepository.findAll(pageable).map(resourceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ResourceDto getById(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto update(UUID id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        if (!resource.getName().equals(request.getName()) && resourceRepository.existsByName(request.getName())) {
            throw new BadRequestException("Resource name already exists");
        }

        resourceMapper.updateEntity(resource, request);
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    @Transactional
    public void delete(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        resourceRepository.delete(resource);
    }
}
