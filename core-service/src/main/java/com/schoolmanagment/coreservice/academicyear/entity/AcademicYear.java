package com.schoolmanagment.coreservice.academicyear.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "tbl_academic_years")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AcademicYear extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ac_year", nullable = false, length = 50)
    private String acYear;

    @Column(nullable = false, length = 20)
    private String semester;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
