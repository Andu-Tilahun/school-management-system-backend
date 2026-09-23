package com.schoolmanagment.coreservice.timetable.mapper;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.timetable.dto.TimetableDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableRequest;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import org.springframework.stereotype.Component;

@Component
public class TimetableMapper {

    public TimetableDto toDto(Timetable timetable) {
        ClassSection classSection = timetable.getClassSection();
        Subject subject = timetable.getSubject();
        Teacher teacher = timetable.getTeacher();
        return TimetableDto.builder()
                .id(timetable.getId())
                .schoolId(timetable.getSchoolId())
                .classSectionId(classSection != null ? classSection.getId() : null)
                .classSectionName(classSection != null ? classSection.getName() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectName(subject != null ? subject.getSubjectName() : null)
                .subjectCode(subject != null ? subject.getSubjectCode() : null)
                .teacherId(teacher != null ? teacher.getId() : null)
                .teacherFullName(buildFullName(teacher))
                .day(timetable.getDay())
                .period(timetable.getPeriod())
                .createdAt(timetable.getCreatedAt())
                .createdByName(timetable.getCreatedByName())
                .updatedByName(timetable.getUpdatedByName())
                .build();
    }

    public Timetable toEntity(
            TimetableRequest request,
            ClassSection classSection,
            Subject subject,
            Teacher teacher
    ) {
        return Timetable.builder()
                .classSection(classSection)
                .subject(subject)
                .teacher(teacher)
                .day(request.getDay())
                .period(request.getPeriod())
                .active(true)
                .build();
    }

    public void updateEntity(
            Timetable timetable,
            TimetableRequest request,
            ClassSection classSection,
            Subject subject,
            Teacher teacher
    ) {
        timetable.setClassSection(classSection);
        timetable.setSubject(subject);
        timetable.setTeacher(teacher);
        timetable.setDay(request.getDay());
        timetable.setPeriod(request.getPeriod());
    }

    private String buildFullName(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        StringBuilder name = new StringBuilder(teacher.getFirstName());
        if (teacher.getMiddleName() != null && !teacher.getMiddleName().isBlank()) {
            name.append(" ").append(teacher.getMiddleName());
        }
        name.append(" ").append(teacher.getLastName());
        return name.toString();
    }
}
