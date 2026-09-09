package com.schoolmanagment.coreservice.subject.dto;

import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectFilterRequest {

    private UUID schoolId;
    private String searchText;
    private SubjectStatus status;
    private String sortBy;
    private String sortDirection = "ASC";
}