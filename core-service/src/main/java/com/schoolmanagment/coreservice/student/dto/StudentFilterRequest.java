package com.schoolmanagment.coreservice.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFilterRequest {

    private String searchText;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
