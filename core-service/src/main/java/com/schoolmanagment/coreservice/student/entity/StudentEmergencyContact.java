package com.schoolmanagment.coreservice.student.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.student.enums.ContactRelationship;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_student_emergency_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentEmergencyContact extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_contact_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EmergencyContact emergencyContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactRelationship relationship;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
