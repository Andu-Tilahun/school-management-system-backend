package com.schoolmanagment.coreservice.subject.entity;

import com.schoolmanagment.coreservice.school.entity.School;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "tbl_subjects",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_subjects_school_subject_code",
                columnNames = {"school_id", "subject_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;
    @Column(name = "subject_name", nullable = false)
    private String subjectName;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubjectStatus status;
}
