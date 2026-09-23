package com.schoolmanagment.coreservice.grade.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_grades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Grade extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "grade", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Subject> subjects = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
