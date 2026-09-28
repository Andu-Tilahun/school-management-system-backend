package com.schoolmanagment.coreservice.exam.specification;

import com.schoolmanagment.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
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

        predicates.add(cb.isTrue(root.get("active")));
        applyPolicyScope(root, query, cb, predicates);

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(studentId(root), filterRequest.getStudentId()));
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
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            ArrayList<Predicate> predicates
    ) {
        UserContext context = UserContext.current();
        if (context.hasStudentPolicy()) {
            context.getCurrentExternalId().ifPresentOrElse(
                    studentId -> predicates.add(cb.equal(studentId(root), studentId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasEmergencyContactPolicy()) {
            context.getCurrentExternalId().ifPresentOrElse(
                    contactId -> predicates.add(linkedToEmergencyContact(root, query, cb, contactId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasPolicy(PolicyNames.TEACHER_POLICY)) {
            context.getCurrentExternalId().ifPresentOrElse(
                    teacherId -> predicates.add(taughtByTeacher(root, query, cb, teacherId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasSchoolAdminPolicy()) {
            context.getCurrentExternalId().ifPresent(
                    schoolId -> predicates.add(cb.equal(root.get("schoolId"), schoolId)));
        }
    }

    private Predicate taughtByTeacher(
            Root<StudentMark> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID teacherId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<Timetable> timetable = subquery.from(Timetable.class);
        subquery.select(timetable.get("id"));
        subquery.where(
                cb.equal(
                        timetable.get("classSection"),
                        root.get("enrollmentTerm").get("enrollment").get("classSection")),
                cb.equal(timetable.get("teacherSubjectAssignment").get("teacher").get("id"), teacherId),
                cb.equal(timetable.get("teacherSubjectAssignment").get("subject"), root.get("subject")),
                cb.isTrue(timetable.get("active")),
                cb.isTrue(timetable.get("teacherSubjectAssignment").get("active"))
        );
        return cb.exists(subquery);
    }

    private Predicate linkedToEmergencyContact(
            Root<StudentMark> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID emergencyContactId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<StudentEmergencyContact> link = subquery.from(StudentEmergencyContact.class);
        subquery.select(link.get("id"));
        subquery.where(
                cb.equal(link.get("student"), root.get("enrollmentTerm").get("enrollment").get("student")),
                cb.equal(link.get("emergencyContact").get("id"), emergencyContactId),
                cb.isTrue(link.get("active")),
                cb.isTrue(link.get("emergencyContact").get("active"))
        );
        return cb.exists(subquery);
    }

    private Path<UUID> studentId(Root<StudentMark> root) {
        return root.get("enrollmentTerm").get("enrollment").get("student").get("id");
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
