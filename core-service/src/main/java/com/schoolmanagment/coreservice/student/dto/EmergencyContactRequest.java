package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.ContactRelationship;
import com.schoolmanagment.coreservice.student.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmergencyContactRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @NotBlank(message = "Sub city is required")
    private String subCity;

    private Integer kebele;

    private String houseNumber;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Relationship is required")
    private ContactRelationship relationship;

    private Boolean isPrimary;
}
