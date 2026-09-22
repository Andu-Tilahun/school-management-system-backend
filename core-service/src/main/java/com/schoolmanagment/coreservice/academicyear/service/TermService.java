package com.schoolmanagment.coreservice.academicyear.service;

import com.schoolmanagment.coreservice.academicyear.dto.TermDto;
import com.schoolmanagment.coreservice.academicyear.dto.TermRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TermService {

    Page<TermDto> getAllTerms(int page, int size, UUID academicYearId);

    TermDto getTermById(UUID id);

    TermDto createTerm(TermRequest request);

    TermDto updateTerm(UUID id, TermRequest request);

    void deleteTerm(UUID id);
}
