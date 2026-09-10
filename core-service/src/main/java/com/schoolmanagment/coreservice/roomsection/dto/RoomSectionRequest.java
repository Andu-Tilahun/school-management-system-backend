package com.schoolmanagment.coreservice.roomsection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RoomSectionRequest {

    @NotNull(message = "Class section is required")
    private UUID classSectionId;

    @NotNull(message = "Room is required")
    private UUID roomId;
}
