package com.schoolmanagment.userservice.resource.mapper;

import com.schoolmanagment.userservice.resource.dto.ResourceDto;
import com.schoolmanagment.userservice.resource.dto.ResourceRequest;
import com.schoolmanagment.userservice.resource.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceDto toDto(Resource resource) {
        if (resource == null) {
            return null;
        }
        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .type(resource.getType())
                .uriPattern(resource.getUriPattern())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    public Resource toEntity(ResourceRequest request) {
        return Resource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .uriPattern(request.getUriPattern())
                .build();
    }

    public void updateEntity(Resource resource, ResourceRequest request) {
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setUriPattern(request.getUriPattern());
    }
}
