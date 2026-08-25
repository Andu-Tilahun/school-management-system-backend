package com.schoolmanagment.commonapplication.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String searchText;

    private Map filters;

    private String sortBy;
    private boolean ascending = true;

    private int page = 0;
    private int size = 10;
}
