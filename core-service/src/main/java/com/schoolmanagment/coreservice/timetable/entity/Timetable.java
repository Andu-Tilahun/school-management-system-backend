package com.schoolmanagment.coreservice.timetable.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_timetables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Timetable extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ClassSection classSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Day day;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Period period;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
