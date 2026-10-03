package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionHomeroomService;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionService;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.mapper.StudentMapper;
import com.schoolmanagment.coreservice.student.repository.EmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.repository.StudentEmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
import com.schoolmanagment.coreservice.student.specification.StudentSpecification;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.service.TeacherService;
import com.schoolmanagment.coreservice.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentMapper studentMapper;
    private final EmergencyContactService emergencyContactService;
    private final ClassSectionService classSectionService;
    @Lazy
    private final TeacherService teacherService;
    @Lazy
    private final TimetableService timetableService;
    @Lazy
    private final ClassSectionHomeroomService classSectionHomeroomService;
    private final StudentEmergencyContactRepository studentEmergencyContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDto> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findByActiveTrue(pageable)
                .map(studentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDto> filterStudents(StudentFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return studentRepository.findAll(new StudentSpecification(request), pageable)
                .map(studentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(UUID id) {
        return studentMapper.toDto(findActiveStudentForDetail(id));
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentRequest request) {
        validateStudentMobileNumberNotTaken(request.getMobileNumber(), null);

        Student student = studentMapper.toEntity(request);

        Student saved = studentRepository.save(student);

        registerEmergencyContacts(saved.getId(), request.getEmergencyContacts());

        return studentMapper.toDto(findActiveStudentForDetail(saved.getId()));
    }

    @Override
    @Transactional
    public StudentDto updateStudent(UUID id, StudentRequest request) {
        Student student = findActiveStudentById(id);
        validateStudentMobileNumberNotTaken(request.getMobileNumber(), id);
        studentMapper.updateEntity(student, request);
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = findActiveStudentById(id);
        student.deactivateWithContactLinks();
        studentRepository.save(student);
    }

    private void registerEmergencyContacts(UUID studentId, List<EmergencyContactRequest> requests) {
        if (requests == null) {
            return;
        }
        for (EmergencyContactRequest request : requests) {
            emergencyContactService.registerEmergencyContact(studentId, request);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByTimetable() {
        Teacher teacher = currentTeacher();
        return findStudentsByClassSections(
                timetableService.findActiveClassSectionIdsByTeacherId(teacher.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByHomeroom() {
        Teacher teacher = currentTeacher();
        return findStudentsByClassSections(
                classSectionHomeroomService.findActiveClassSectionIdsByTeacherId(teacher.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByClassSection(UUID classSectionId) {
        classSectionService.findActiveClassSectionById(classSectionId);
        return findStudentsByClassSections(List.of(classSectionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsForCurrentEmergencyContact() {
        UUID emergencyContactId = currentEmergencyContactId();
        return studentEmergencyContactRepository.findActiveStudentsByEmergencyContactId(emergencyContactId).stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public Student findActiveStudentById(UUID id) {
        return studentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private Student findActiveStudentForDetail(UUID id) {
        Student student = findActiveStudentById(id);
        initializeEnrollmentsForDetail(student);
        return student;
    }

    private void initializeEnrollmentsForDetail(Student student) {
        Hibernate.initialize(student.getEnrollments());
        if (student.getEnrollments() == null) {
            return;
        }
        for (Enrollment enrollment : student.getEnrollments()) {
            if (enrollment.getClassSection() != null) {
                Hibernate.initialize(enrollment.getClassSection());
                if (enrollment.getClassSection().getGrade() != null) {
                    Hibernate.initialize(enrollment.getClassSection().getGrade());
                }
            }
            Hibernate.initialize(enrollment.getEnrollmentTerms());
            if (enrollment.getEnrollmentTerms() == null) {
                continue;
            }
            for (EnrollmentTerm enrollmentTerm : enrollment.getEnrollmentTerms()) {
                if (enrollmentTerm.getTerm() == null) {
                    continue;
                }
                Hibernate.initialize(enrollmentTerm.getTerm());
                if (enrollmentTerm.getTerm().getAcademicYear() != null) {
                    Hibernate.initialize(enrollmentTerm.getTerm().getAcademicYear());
                }
            }
        }
    }

    private UUID currentEmergencyContactId() {
        UUID emergencyContactId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in emergency contact has no external id"));
        EmergencyContact contact = emergencyContactRepository.findById(emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emergency contact not found with id: " + emergencyContactId));
        if (!Boolean.TRUE.equals(contact.getActive())) {
            throw new BadRequestException("Emergency contact is not active");
        }
        return emergencyContactId;
    }

    private Teacher currentTeacher() {
        UUID teacherId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in teacher has no external id"));
        return teacherService.findActiveTeacherById(teacherId);
    }

    private List<StudentDto> findStudentsByClassSections(Collection<UUID> classSectionIds) {
        if (classSectionIds == null || classSectionIds.isEmpty()) {
            return List.of();
        }
        return enrollmentRepository
                .findActiveStudentsByClassSectionIds(classSectionIds, EnrollmentStatus.ACTIVE)
                .stream()
                .map(studentMapper::toDto)
                .toList();
    }

    private void validateStudentMobileNumberNotTaken(String mobileNumber, UUID excludeId) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return;
        }
        String normalized = mobileNumber.trim();
        studentRepository.findByMobileNumber(normalized).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Student with mobile number '" + normalized + "' already exists in this school");
            }
        });
    }
}
