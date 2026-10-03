package com.schoolmanagment.coreservice.teacher.repository;

import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeacherSubjectAssignmentRepository
        extends JpaRepository<TeacherSubjectAssignment, UUID> {

    List<TeacherSubjectAssignment> findByTeacherId(UUID teacherId);

    List<TeacherSubjectAssignment> findByTeacherIdAndActiveTrue(UUID teacherId);

    @Query("""
            SELECT a
            FROM TeacherSubjectAssignment a
            JOIN FETCH a.subject s
            WHERE a.teacher.id = :teacherId
              AND a.active = true
              AND s.status = :status
            ORDER BY s.subjectName ASC
            """)
    List<TeacherSubjectAssignment> findActiveByTeacherIdWithSubject(
            @Param("teacherId") UUID teacherId,
            @Param("status") SubjectStatus status);

    List<TeacherSubjectAssignment> findBySubjectIdAndActiveTrue(UUID subjectId);

    @Query("""
            SELECT a
            FROM TeacherSubjectAssignment a
            JOIN FETCH a.teacher t
            JOIN FETCH a.subject s
            WHERE s.id = :subjectId
              AND a.active = true
              AND t.active = true
              AND s.status = :status
            ORDER BY t.firstName ASC, t.lastName ASC
            """)
    List<TeacherSubjectAssignment> findActiveBySubjectIdWithTeacher(
            @Param("subjectId") UUID subjectId,
            @Param("status") SubjectStatus status);

    boolean existsByTeacherIdAndSubjectId(UUID teacherId, UUID subjectId);
}
