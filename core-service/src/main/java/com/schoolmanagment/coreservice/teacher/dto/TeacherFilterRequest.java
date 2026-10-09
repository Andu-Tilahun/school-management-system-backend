package com.schoolmanagment.coreservice.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherFilterRequest {

    private UUID schoolId;

    private String searchText;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;

    @Builder.Default
    private Boolean active = true;

    /** Null means both teachers with and without an account. */
    private Boolean hasAccount;
}
