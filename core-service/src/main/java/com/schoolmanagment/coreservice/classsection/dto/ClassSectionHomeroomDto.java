package com.schoolmanagment.coreservice.classsection.dto;

import com.schoolmanagment.coreservice.classsection.entity.ClassSectionHomeroom;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionHomeroomDto {

    private UUID id;

    private UUID classSectionId;
    private String classSectionName;

    private UUID teacherId;
    private String teacherFullName;

    private Boolean active;

    public static ClassSectionHomeroomDto fromEntity(ClassSectionHomeroom homeroom) {
        return ClassSectionHomeroomDto.builder()
                .id(homeroom.getId())
                .classSectionId(homeroom.getClassSection().getId())
                .classSectionName(homeroom.getClassSection().getName())
                .teacherId(homeroom.getTeacher().getId())
                .teacherFullName(buildFullName(homeroom.getTeacher()))
                .active(homeroom.getActive())
                .build();
    }

    private static String buildFullName(Teacher teacher) {
        StringBuilder sb = new StringBuilder(teacher.getFirstName());
        if (teacher.getMiddleName() != null && !teacher.getMiddleName().isBlank()) {
            sb.append(" ").append(teacher.getMiddleName());
        }
        sb.append(" ").append(teacher.getLastName());
        return sb.toString();
    }
}
