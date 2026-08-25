package com.schoolmanagment.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFilterRequest {

    private String searchText;

    /** Policy names (e.g. {@code OPERATOR_POLICY}); matches direct or group policies. */
    private List<String> policyNames;

    private List<String> genders;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
