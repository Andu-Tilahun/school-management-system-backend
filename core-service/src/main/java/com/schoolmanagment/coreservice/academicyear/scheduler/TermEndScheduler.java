package com.schoolmanagment.coreservice.academicyear.scheduler;

import com.schoolmanagment.coreservice.academicyear.entity.Term;
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
public class TermEndScheduler {

    static final ZoneId ETHIOPIA = ZoneId.of("Africa/Addis_Ababa");

    private final TermRepository termRepository;
    private final EnrollmentTermRepository enrollmentTermRepository;

    /**
     * Midnight (12:00 at night) in Ethiopia, every day.
     * A term stays active through its end date and closes once that date has passed.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Africa/Addis_Ababa")
    @Transactional
    public void endTermsPastEndDate() {
        LocalDate today = LocalDate.now(ETHIOPIA);
        List<Term> ended = termRepository.findActiveWithEndDateBefore(today);
        if (ended.isEmpty()) {
            return;
        }

        ended.forEach(term -> term.setActive(false));
        termRepository.saveAll(ended);

        int registrations = enrollmentTermRepository.completeActiveByTermIds(
                ended.stream().map(Term::getId).toList(),
                EnrollmentTermStatus.COMPLETED,
                EnrollmentTermStatus.ACTIVE);

        log.info("Ended {} term(s) and completed {} enrollment term(s) as of {}",
                ended.size(), registrations, today);
    }
}
