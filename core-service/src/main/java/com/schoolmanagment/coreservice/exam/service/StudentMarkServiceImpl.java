package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.entity.SubjectTotal;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.PassFailStatus;
import com.schoolmanagment.coreservice.exam.mapper.StudentMarkMapper;
import com.schoolmanagment.coreservice.exam.repository.StudentMarkRepository;
import com.schoolmanagment.coreservice.exam.specification.StudentMarkSpecification;
import com.schoolmanagment.coreservice.exam.repository.SubjectTotalRepository;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.repository.EmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import com.schoolmanagment.coreservice.student.repository.StudentEmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import com.schoolmanagment.coreservice.subject.repository.SubjectRepository;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.repository.TeacherRepository;
import com.schoolmanagment.coreservice.teacher.service.TeacherService;
import com.schoolmanagment.coreservice.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentMarkServiceImpl implements StudentMarkService {

    private final StudentMarkRepository studentMarkRepository;
    private final SubjectTotalRepository subjectTotalRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentTermRepository enrollmentTermRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final StudentEmergencyContactRepository studentEmergencyContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final TimetableRepository timetableRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherService teacherService;
    private final StudentMarkMapper studentMarkMapper;

    @Override
    @Transactional
    public StudentMarkDto register(StudentMarkRequest request) {

        EnrollmentTerm enrollmentTerm = resolveActiveEnrollmentTerm(request.getStudentId());

        if (enrollmentTerm.getStatus() != EnrollmentTermStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot register a mark for a non-active term registration");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        studentMarkRepository
                .findByEnrollmentTermIdAndSubjectIdAndType(
                        enrollmentTerm.getId(), subject.getId(), request.getType())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Student is already registered for this subject/type");
                });

        boolean hasMark = request.getStudMark() != null;

        boolean isAbsent = Boolean.TRUE.equals(request.getMarkAbsent());

        if (hasMark && isAbsent) {
            throw new IllegalArgumentException(
                    "Cannot provide both studMark and markAbsent=true");
        }

        StudentMark mark = studentMarkMapper.toEntity(request, enrollmentTerm, subject);

        if (isAbsent) {
            mark.setStatus(MarkStatus.ABSENT);
            mark.setStudMark(null);
        } else if (hasMark) {
            mark.setStudMark(request.getStudMark());
            mark.setStatus(MarkStatus.GRADED);
        }

        StudentMark saved = studentMarkRepository.save(mark);

        if (saved.getStatus() == MarkStatus.GRADED || saved.getStatus() == MarkStatus.ABSENT) {
            recalculate(saved.getEnrollmentTerm().getId(), saved.getSubject().getId());
        }

        return studentMarkMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMarkDto> filter(StudentMarkFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return studentMarkRepository.findAll(new StudentMarkSpecification(request), pageable)
                .map(studentMarkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsForCurrentTeacher() {
        return teacherService.getAssignedSubjects();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsForCurrentStudent() {
        UUID studentId = currentStudentId();
        studentRepository.findByIdAndActiveTrue(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return subjectsForStudent(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsForCurrentEmergencyContact() {
        UUID emergencyContactId = currentEmergencyContactId();
        return studentEmergencyContactRepository.findActiveStudentsByEmergencyContactId(emergencyContactId).stream()
                .map(StudentDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsForEmergencyContactStudent(UUID studentId) {
        UUID emergencyContactId = currentEmergencyContactId();
        if (!studentEmergencyContactRepository
                .existsByStudent_IdAndEmergencyContact_IdAndActiveTrue(studentId, emergencyContactId)) {
            throw new BadRequestException("Student is not linked to the logged-in emergency contact");
        }
        studentRepository.findByIdAndActiveTrue(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return subjectsForStudent(studentId);
    }

    private List<TeacherSubjectAssignmentDto> subjectsForStudent(UUID studentId) {
        return enrollmentRepository
                .findActiveClassSectionIdByStudentId(studentId, EnrollmentStatus.ACTIVE)
                .map(classSectionId -> timetableRepository
                        .findActiveSubjectsByClassSectionId(classSectionId, SubjectStatus.ACTIVE)
                        .stream()
                        .map(this::toSubjectDto)
                        .toList())
                .orElseGet(List::of);
    }

    private EnrollmentTerm resolveActiveEnrollmentTerm(UUID studentId) {
        List<EnrollmentTerm> enrollmentTerms = enrollmentTermRepository.findActiveByStudentIdAndSchoolId(
                studentId,
                currentSchoolId(),
                EnrollmentStatus.ACTIVE,
                EnrollmentTermStatus.ACTIVE);
        if (enrollmentTerms.isEmpty()) {
            throw new ResourceNotFoundException("Active enrollment term not found for student: " + studentId);
        }
        if (enrollmentTerms.size() > 1) {
            throw new BadRequestException("Student has more than one active enrollment term: " + studentId);
        }
        return enrollmentTerms.get(0);
    }

    private TeacherSubjectAssignmentDto toSubjectDto(Subject subject) {
        return TeacherSubjectAssignmentDto.builder()
                .id(subject.getId())
                .subjectId(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .active(subject.getStatus() == SubjectStatus.ACTIVE)
                .build();
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

    private UUID currentStudentId() {
        return UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in student has no external id"));
    }

    private Teacher currentTeacher() {
        UUID teacherId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in teacher has no external id"));
        return teacherRepository.findByIdAndActiveTrue(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
    }

    private UUID currentSchoolId() {
        UUID schoolId = currentTeacher().getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException(
                    "No schoolId on the current teacher — cannot save a school-scoped entity without one.");
        }
        return schoolId;
    }

    @Transactional
    public SubjectTotal recalculate(UUID enrollmentTermId, UUID subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        EnrollmentTerm enrollmentTerm = enrollmentTermRepository.findById(enrollmentTermId)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentTerm not found"));

        List<StudentMark> gradedMarks = studentMarkRepository
                .findByEnrollmentTermIdAndSubjectIdAndStatusAndActiveTrue(
                        enrollmentTermId, subjectId, MarkStatus.GRADED);

        double weightedSum = 0.0;
        double weightUsed = 0.0;

        for (StudentMark mark : gradedMarks) {
            Double weight = mark.getTotalMarkWeight();
            if (weight == null || mark.getStudMark() == null) {
                continue;
            }
            weightedSum += mark.getStudMark() * weight;
            weightUsed += weight;
        }

        if (weightUsed == 0.0) {
            throw new IllegalStateException(
                    "No graded marks with defined weights exist yet for this subject/term");
        }

        double totalMark = weightedSum / weightUsed;

        PassFailStatus status = totalMark >= 50
                ? PassFailStatus.PASS
                : PassFailStatus.FAIL;

        SubjectTotal subjectTotal = subjectTotalRepository
                .findByEnrollmentTermIdAndSubjectId(enrollmentTermId, subjectId)
                .orElseGet(() -> SubjectTotal.builder()
                        .enrollmentTerm(enrollmentTerm)
                        .subject(subject)
                        .active(true)
                        .build());

        subjectTotal.setTotalMark(totalMark);
        subjectTotal.setStatus(status);

        return subjectTotalRepository.save(subjectTotal);
    }
}
