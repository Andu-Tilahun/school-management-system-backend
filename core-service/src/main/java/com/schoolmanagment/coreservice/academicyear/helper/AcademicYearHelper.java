package com.schoolmanagment.coreservice.academicyear.helper;

import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.repository.AcademicYearRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;

@Component
public class AcademicYearHelper {

    private static AcademicYearHelper instance;

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearHelper(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    @PostConstruct
    private void registerStaticInstance() {
        instance = this;
    }

    public static AcademicYear getActiveAcademicYear() {
        return getActiveAcademicYear(currentSchoolId());
    }

    public static AcademicYear getActiveAcademicYear(UUID schoolId) {
        return helper().academicYearRepository.findBySchoolIdAndActiveTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active academic year not found for this school"));
    }

    private static AcademicYearHelper helper() {
        if (instance == null) {
            throw new IllegalStateException("AcademicYearHelper has not been initialized");
        }
        return instance;
    }

    private static UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot resolve the active academic year without one."));
    }
}
