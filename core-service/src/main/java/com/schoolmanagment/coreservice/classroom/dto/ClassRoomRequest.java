package com.schoolmanagment.coreservice.classroom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassRoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room size is required")
    @Min(value = 1, message = "Room size must be at least 1")
    private Integer roomSize;
}
