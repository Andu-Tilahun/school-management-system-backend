package com.schoolmanagment.coreservice.academicyear.scheduler;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.repository.AcademicYearRepository;
import com.schoolmanagment.coreservice.academicyear.repository.TermRepository;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicYearEndScheduler {

    static final ZoneId ETHIOPIA = ZoneId.of("Africa/Addis_Ababa");

    private final AcademicYearRepository academicYearRepository;
    private final TermRepository termRepository;
    private final EnrollmentTermRepository enrollmentTermRepository;

    /**
     * Midnight (12:00 at night) in Ethiopia, every day.
     * An academic year stays active through its end date and closes once that date has passed.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Africa/Addis_Ababa")
    @Transactional
    public void endAcademicYearsPastEndDate() {
        LocalDate today = LocalDate.now(ETHIOPIA);
        List<AcademicYear> ended = academicYearRepository.findActiveWithEndDateBefore(today);
        if (ended.isEmpty()) {
            return;
        }

        ended.forEach(year -> year.setActive(false));
        academicYearRepository.saveAll(ended);

        List<Term> openTerms = termRepository.findActiveByAcademicYearIds(
                ended.stream().map(AcademicYear::getId).toList());
        int registrations = 0;
        if (!openTerms.isEmpty()) {
            openTerms.forEach(term -> term.setActive(false));
            termRepository.saveAll(openTerms);
            registrations = enrollmentTermRepository.completeActiveByTermIds(
                    openTerms.stream().map(Term::getId).toList(),
                    EnrollmentTermStatus.COMPLETED,
                    EnrollmentTermStatus.ACTIVE);
        }

        log.info("Ended {} academic year(s), closed {} term(s), and completed {} enrollment term(s) as of {}",
                ended.size(), openTerms.size(), registrations, today);
    }
}
