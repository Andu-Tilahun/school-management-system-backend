package com.schoolmanagment.coreservice.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String middleName;

    private String lastName;

    private int age;

    private String sex;

    private String nationality;

    private String subCity;

    private int kebele;

    private String houseNumber;

    private String mobileNumber;
}
