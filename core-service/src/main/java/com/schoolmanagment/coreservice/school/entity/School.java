package com.schoolmanagment.coreservice.school.entity;

import com.schoolmanagment.coreservice.school.enums.SchoolType;
import com.schoolmanagment.coreservice.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_schools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "school_type", nullable = false)
    private SchoolType schoolType;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "website")
    private String website;
}