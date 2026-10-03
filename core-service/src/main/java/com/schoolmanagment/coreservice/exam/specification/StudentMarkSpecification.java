package com.schoolmanagment.coreservice.exam.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class StudentMarkSpecification implements Specification<StudentMark> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id",
            "createdAt",
            "type",
            "status",
            "studMark",
            "totalMarkWeight"
    );

    private final StudentMarkFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<StudentMark> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        Join<Object, Object> enrollment = root.join("enrollmentTerm", JoinType.INNER)
                .join("enrollment", JoinType.INNER);
        Join<Object, Object> student = enrollment.join("student", JoinType.INNER);

        if (filterRequest.getActive() != null) {
            predicates.add(cb.equal(root.get("active"), filterRequest.getActive()));
        }

        UserContext.current().getCurrentExternalId()
                .ifPresent(schoolId -> predicates.add(cb.equal(root.get("schoolId"), schoolId)));

        applyPolicyScope(root, enrollment, student, query, cb, predicates);

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(student.get("id"), filterRequest.getStudentId()));
        }

        if (filterRequest.getClassSectionId() != null) {
            predicates.add(cb.equal(
                    enrollment.get("classSection").get("id"),
                    filterRequest.getClassSectionId()));
        }

        if (filterRequest.getSubjectId() != null) {
            predicates.add(cb.equal(root.get("subject").get("id"), filterRequest.getSubjectId()));
        }

        if (filterRequest.getType() != null) {
            predicates.add(cb.equal(root.get("type"), filterRequest.getType()));
        }

        if (filterRequest.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
        }

        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            applySorting(root, query, cb);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applyPolicyScope(
            Root<StudentMark> root,
            Join<Object, Object> enrollment,
            Join<Object, Object> student,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            ArrayList<Predicate> predicates
    ) {
        UserContext context = UserContext.current();
        if (context.hasStudentPolicy()) {
            currentPersonId().ifPresentOrElse(
                    studentId -> predicates.add(cb.equal(student.get("id"), studentId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasEmergencyContactPolicy()) {
            currentPersonId().ifPresentOrElse(
                    contactId -> predicates.add(linkedToEmergencyContact(student, query, cb, contactId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasTeacherPolicy()) {
            currentPersonId().ifPresentOrElse(
                    teacherId -> predicates.add(taughtByTeacher(root, enrollment, query, cb, teacherId)),
                    () -> predicates.add(cb.disjunction()));
        }
    }

    /**
     * Teacher, student, and emergency-contact records use the login user id.
     * {@code externalId} on those accounts is the school id.
     */
    private Optional<UUID> currentPersonId() {
        try {
            return Optional.ofNullable(UserContext.current().getCurrentUserId());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private Predicate taughtByTeacher(
            Root<StudentMark> root,
            Join<Object, Object> enrollment,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID teacherId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<Timetable> timetable = subquery.from(Timetable.class);
        subquery.select(timetable.get("id"));
        subquery.where(
                cb.equal(
                        timetable.get("classSection").get("id"),
                        enrollment.get("classSection").get("id")),
                cb.equal(timetable.get("teacherSubjectAssignment").get("teacher").get("id"), teacherId),
                cb.equal(
                        timetable.get("teacherSubjectAssignment").get("subject").get("id"),
                        root.get("subject").get("id")),
                cb.isTrue(timetable.get("active")),
                cb.isTrue(timetable.get("teacherSubjectAssignment").get("active"))
        );
        return cb.exists(subquery);
    }

    private Predicate linkedToEmergencyContact(
            Join<Object, Object> student,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID emergencyContactId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<StudentEmergencyContact> link = subquery.from(StudentEmergencyContact.class);
        subquery.select(link.get("id"));
        subquery.where(
                cb.equal(link.get("student").get("id"), student.get("id")),
                cb.equal(link.get("emergencyContact").get("id"), emergencyContactId),
                cb.isTrue(link.get("active")),
                cb.isTrue(link.get("emergencyContact").get("active"))
        );
        return cb.exists(subquery);
    }

    private void applySorting(Root<StudentMark> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }

        boolean ascending = "ASC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
