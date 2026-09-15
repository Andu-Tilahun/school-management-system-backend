package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Student;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentMapper {

    public StudentDto toDto(Student student) {
        return StudentDto.fromEntity(student);
    }

    public Student toEntity(StudentRequest request) {
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .subCity(request.getSubCity())
                .kebele(request.getKebele())
                .houseNumber(request.getHouseNumber())
                .mobileNumber(request.getMobileNumber())
                .active(true)
                .build();
        addContacts(student, request.getEmergencyContacts());
        return student;
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        student.setBirthDate(request.getBirthDate());
        student.setGender(request.getGender());
        student.setNationality(request.getNationality());
        student.setSubCity(request.getSubCity());
        student.setKebele(request.getKebele());
        student.setHouseNumber(request.getHouseNumber());
        student.setMobileNumber(request.getMobileNumber());
        replaceContacts(student, request.getEmergencyContacts());
    }

    private void replaceContacts(Student student, List<EmergencyContactRequest> requests) {
        if (student.getEmergencyContacts() == null) {
            student.setEmergencyContacts(new ArrayList<>());
        } else {
            student.getEmergencyContacts().clear();
        }
        addContacts(student, requests);
    }

    private void addContacts(Student student, List<EmergencyContactRequest> requests) {
        if (requests == null) {
            return;
        }
        for (EmergencyContactRequest request : requests) {
            student.addEmergencyContact(toContactEntity(request, student));
        }
    }

    private EmergencyContact toContactEntity(EmergencyContactRequest request, Student student) {
        EmergencyContact contact = EmergencyContact.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .subCity(request.getSubCity())
                .kebele(request.getKebele())
                .houseNumber(request.getHouseNumber())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .active(true)
                .build();
        if (student.getSchoolId() != null) {
            contact.setSchoolId(student.getSchoolId());
        }
        return contact;
    }
}
