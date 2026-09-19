package com.schoolmanagment.coreservice.student.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.student.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Student extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(nullable = false, length = 100)
    private String nationality;

    @Column(name = "sub_city", nullable = false, length = 100)
    private String subCity;

    @Column
    private Integer kebele;

    @Column(name = "house_number", length = 50)
    private String houseNumber;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StudentEmergencyContact> emergencyContactLinks = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments = new ArrayList<>();

    public void deactivateWithContactLinks() {
        this.active = false;
        if (emergencyContactLinks == null) {
            return;
        }
        for (StudentEmergencyContact link : emergencyContactLinks) {
            link.setActive(false);
        }
    }
}
