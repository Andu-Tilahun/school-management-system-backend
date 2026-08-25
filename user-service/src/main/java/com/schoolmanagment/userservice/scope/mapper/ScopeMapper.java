package com.schoolmanagment.userservice.scope.mapper;

import com.schoolmanagment.userservice.scope.dto.ScopeDto;
import com.schoolmanagment.userservice.scope.dto.ScopeRequest;
import com.schoolmanagment.userservice.scope.entity.Scope;
import org.springframework.stereotype.Component;

@Component
public class ScopeMapper {

    public ScopeDto toDto(Scope scope) {
        if (scope == null) {
            return null;
        }
        return ScopeDto.builder()
                .id(scope.getId())
                .name(scope.getName())
                .description(scope.getDescription())
                .createdAt(scope.getCreatedAt())
                .build();
    }

    public Scope toEntity(ScopeRequest request) {
        return Scope.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(Scope scope, ScopeRequest request) {
        scope.setName(request.getName());
        scope.setDescription(request.getDescription());
    }
}
