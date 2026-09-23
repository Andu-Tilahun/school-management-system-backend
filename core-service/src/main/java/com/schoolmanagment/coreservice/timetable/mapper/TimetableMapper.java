package com.schoolmanagment.coreservice.timetable.mapper;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import com.schoolmanagment.coreservice.timetable.dto.TimetableDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableRequest;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import org.springframework.stereotype.Component;

@Component
public class TimetableMapper {

    public TimetableDto toDto(Timetable timetable) {
        ClassSection classSection = timetable.getClassSection();
        TeacherSubjectAssignment assignment = timetable.getTeacherSubjectAssignment();
        Subject subject = assignment != null ? assignment.getSubject() : null;
        Teacher teacher = assignment != null ? assignment.getTeacher() : null;
        return TimetableDto.builder()
                .id(timetable.getId())
                .schoolId(timetable.getSchoolId())
                .classSectionId(classSection != null ? classSection.getId() : null)
                .classSectionName(classSection != null ? classSection.getName() : null)
                .teacherSubjectAssignmentId(assignment != null ? assignment.getId() : null)
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
            TeacherSubjectAssignment teacherSubjectAssignment
    ) {
        return Timetable.builder()
                .classSection(classSection)
                .teacherSubjectAssignment(teacherSubjectAssignment)
                .day(request.getDay())
                .period(request.getPeriod())
                .active(true)
                .build();
    }

    public void updateEntity(
            Timetable timetable,
            TimetableRequest request,
            ClassSection classSection,
            TeacherSubjectAssignment teacherSubjectAssignment
    ) {
        timetable.setClassSection(classSection);
        timetable.setTeacherSubjectAssignment(teacherSubjectAssignment);
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
