package com.schoolmanagment.coreservice.academicyear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearFilterRequest {

    private UUID schoolId;

    private String searchText;

    private String semester;

    private Boolean active = true;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
