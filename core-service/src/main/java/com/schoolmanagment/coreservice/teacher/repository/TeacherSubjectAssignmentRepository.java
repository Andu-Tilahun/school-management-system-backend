package com.schoolmanagment.coreservice.teacher.repository;

import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeacherSubjectAssignmentRepository
        extends JpaRepository<TeacherSubjectAssignment, UUID> {

    List<TeacherSubjectAssignment> findByTeacherId(UUID teacherId);

    List<TeacherSubjectAssignment> findByTeacherIdAndActiveTrue(UUID teacherId);

    List<TeacherSubjectAssignment> findBySubjectIdAndActiveTrue(UUID subjectId);

    boolean existsByTeacherIdAndSubjectId(UUID teacherId, UUID subjectId);
}
