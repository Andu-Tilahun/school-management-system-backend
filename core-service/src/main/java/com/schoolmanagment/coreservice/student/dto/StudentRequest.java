package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StudentRequest {

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

    @Size(max = 20, message = "Mobile number must be at most 20 characters")
    private String mobileNumber;

    @NotEmpty(message = "At least one emergency contact is required")
    @Valid
    private List<EmergencyContactRequest> emergencyContacts;
}
