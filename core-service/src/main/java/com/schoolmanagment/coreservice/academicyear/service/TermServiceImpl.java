package com.schoolmanagment.coreservice.academicyear.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.academicyear.dto.TermDto;
import com.schoolmanagment.coreservice.academicyear.dto.TermRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import com.schoolmanagment.coreservice.academicyear.mapper.TermMapper;
import com.schoolmanagment.coreservice.academicyear.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;
    private final AcademicYearService academicYearService;
    private final TermMapper termMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TermDto> getAllTerms(int page, int size, UUID academicYearId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Term> terms = academicYearId == null
                ? termRepository.findByActiveTrue(pageable)
                : termRepository.findByAcademicYearIdAndActiveTrue(academicYearId, pageable);
        return terms.map(termMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TermDto getTermById(UUID id) {
        return termMapper.toDto(findActiveTermById(id));
    }

    @Override
    @Transactional
    public TermDto createTerm(TermRequest request) {
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());
        validateSemesterNotTaken(academicYear.getId(), request.getSemester(), null);
        validateTermDates(academicYear, request);

        Term term = termMapper.toEntity(request, academicYear);
        term.setSchoolId(academicYear.getSchoolId());
        return termMapper.toDto(termRepository.save(term));
    }

    @Override
    @Transactional
    public TermDto updateTerm(UUID id, TermRequest request) {
        Term term = findActiveTermById(id);
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());
        validateSemesterNotTaken(academicYear.getId(), request.getSemester(), id);
        validateTermDates(academicYear, request);
        termMapper.updateEntity(term, request, academicYear);
        return termMapper.toDto(termRepository.save(term));
    }

    @Override
    @Transactional
    public void deleteTerm(UUID id) {
        Term term = findActiveTermById(id);
        term.setActive(false);
        termRepository.save(term);
    }

    private Term findActiveTermById(UUID id) {
        return termRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));
    }

    private AcademicYear findAcademicYear(UUID academicYearId) {
        return academicYearService.findActiveAcademicYearById(academicYearId);
    }

    private void validateSemesterNotTaken(UUID academicYearId, Semester semester, UUID excludeId) {
        termRepository.findByAcademicYearIdAndSemester(academicYearId, semester)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(excludeId)) {
                        throw new BadRequestException(
                                "Semester " + semester + " already exists in this academic year");
                    }
                });
    }

    private void validateTermDates(AcademicYear academicYear, TermRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Term start date must be on or before the end date");
        }
        LocalDate yearStart = academicYear.getStartDate();
        LocalDate yearEnd = academicYear.getEndDate();
        if (yearStart != null && request.getStartDate() != null && request.getStartDate().isBefore(yearStart)) {
            throw new BadRequestException("Term starts before the academic year");
        }
        if (yearEnd != null && request.getEndDate() != null && request.getEndDate().isAfter(yearEnd)) {
            throw new BadRequestException("Term ends after the academic year");
        }
    }
}
