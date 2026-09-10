package com.schoolmanagment.coreservice.academicyear.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearDto;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearFilterRequest;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.mapper.AcademicYearMapper;
import com.schoolmanagment.coreservice.academicyear.repository.AcademicYearRepository;
import com.schoolmanagment.coreservice.academicyear.specification.AcademicYearSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper academicYearMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AcademicYearDto> getAllAcademicYears(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return academicYearRepository.findByActiveTrue(pageable)
                .map(academicYearMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademicYearDto> filterAcademicYears(AcademicYearFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return academicYearRepository.findAll(new AcademicYearSpecification(request), pageable)
                .map(academicYearMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearDto getAcademicYearById(UUID id) {
        return academicYearMapper.toDto(findActiveAcademicYearById(id));
    }

    @Override
    @Transactional
    public AcademicYearDto createAcademicYear(AcademicYearRequest request) {
        UUID schoolId = currentSchoolId();
        validateYearSemesterNotTaken(schoolId, request.getAcYear(), request.getSemester(), null);

        AcademicYear academicYear = academicYearMapper.toEntity(request);
        academicYear.setSchoolId(schoolId);

        if (Boolean.TRUE.equals(academicYear.getActive())) {
            deactivateOtherYears(schoolId, null);
        }

        return academicYearMapper.toDto(academicYearRepository.save(academicYear));
    }

    @Override
    @Transactional
    public AcademicYearDto updateAcademicYear(UUID id, AcademicYearRequest request) {
        AcademicYear academicYear = findActiveAcademicYearById(id);
        validateYearSemesterNotTaken(
                academicYear.getSchoolId(),
                request.getAcYear(),
                request.getSemester(),
                id
        );
        academicYearMapper.updateEntity(academicYear, request);

        if (Boolean.TRUE.equals(academicYear.getActive())) {
            deactivateOtherYears(academicYear.getSchoolId(), academicYear.getId());
        }

        return academicYearMapper.toDto(academicYearRepository.save(academicYear));
    }

    @Override
    @Transactional
    public void deleteAcademicYear(UUID id) {
        AcademicYear academicYear = findActiveAcademicYearById(id);
        academicYear.setActive(false);
        academicYearRepository.save(academicYear);
    }

    private AcademicYear findActiveAcademicYearById(UUID id) {
        return academicYearRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + id));
    }

    private void validateYearSemesterNotTaken(UUID schoolId, String acYear, String semester, UUID excludeId) {
        academicYearRepository.findBySchoolIdAndAcYearAndSemester(schoolId, acYear, semester)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(excludeId)) {
                        throw new BadRequestException(
                                "Academic year '" + acYear + "' with semester '" + semester
                                        + "' already exists in this school");
                    }
                });
    }

    private void deactivateOtherYears(UUID schoolId, UUID excludeId) {
        academicYearRepository.findAllBySchoolIdAndActiveTrue(schoolId).forEach(year -> {
            if (excludeId == null || !year.getId().equals(excludeId)) {
                year.setActive(false);
                academicYearRepository.save(year);
            }
        });
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
