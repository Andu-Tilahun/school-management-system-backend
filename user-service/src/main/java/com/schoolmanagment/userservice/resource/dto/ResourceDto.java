package com.schoolmanagment.userservice.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private UUID id;
    private String name;
    private String description;
    private String type;
    private String uriPattern;
    private LocalDateTime createdAt;
}
