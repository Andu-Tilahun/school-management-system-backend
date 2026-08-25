package com.schoolmanagment.userservice.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private String description;

    @NotBlank(message = "Type is required")
    @Size(max = 100, message = "Type must be at most 100 characters")
    private String type;

    @Size(max = 500, message = "URI pattern must be at most 500 characters")
    private String uriPattern;
}
