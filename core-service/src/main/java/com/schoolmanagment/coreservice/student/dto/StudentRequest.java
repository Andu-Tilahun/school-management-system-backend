package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.Sex;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;

    @NotNull(message = "Sex is required")
    private Sex sex;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @NotBlank(message = "Sub city is required")
    private String subCity;

    private Integer kebele;

    private String houseNumber;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;
}
