package com.schoolmanagment.coreservice.subject.repository;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID>, JpaSpecificationExecutor<Subject> {
    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByGradeLevel(Integer gradeLevel);

    List<Subject> findByStatus(SubjectStatus status);

    List<Subject> findByGradeLevelAndStatus(Integer gradeLevel, SubjectStatus status);
    boolean existsBySubjectCode(String subjectCode);
    boolean existsBySubjectNameAndGradeLevel(String subjectName, Integer gradeLevel);
}
