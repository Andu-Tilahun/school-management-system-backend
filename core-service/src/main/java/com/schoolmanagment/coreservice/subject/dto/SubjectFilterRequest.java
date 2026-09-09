package com.schoolmanagment.coreservice.subject.dto;

import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectFilterRequest {

    private String searchText;
    private Integer gradeLevel;
    private SubjectStatus status;
    private String sortBy;
    private String sortDirection = "ASC";
}