package com.schoolmanagment.coreservice.timetable.repository;

import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, UUID>, JpaSpecificationExecutor<Timetable> {

    Page<Timetable> findByActiveTrue(Pageable pageable);

    Optional<Timetable> findByIdAndActiveTrue(UUID id);

    boolean existsByClassSectionIdAndDayAndPeriodAndActiveTrue(UUID classSectionId, Day day, Period period);

    boolean existsByClassSectionIdAndDayAndPeriodAndActiveTrueAndIdNot(
            UUID classSectionId, Day day, Period period, UUID id);

    boolean existsByTeacherIdAndDayAndPeriodAndActiveTrue(UUID teacherId, Day day, Period period);

    boolean existsByTeacherIdAndDayAndPeriodAndActiveTrueAndIdNot(
            UUID teacherId, Day day, Period period, UUID id);
}
