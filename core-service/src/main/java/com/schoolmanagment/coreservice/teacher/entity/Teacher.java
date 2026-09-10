package com.schoolmanagment.coreservice.teacher.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.student.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tbl_teachers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Teacher extends SchoolAuditable {

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
}
