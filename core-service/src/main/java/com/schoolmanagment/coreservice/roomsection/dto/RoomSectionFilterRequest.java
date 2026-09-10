package com.schoolmanagment.coreservice.roomsection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSectionFilterRequest {

    private UUID schoolId;

    private UUID classSectionId;

    private UUID roomId;

    private String searchText;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
