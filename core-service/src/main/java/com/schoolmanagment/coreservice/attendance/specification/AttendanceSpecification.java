package com.schoolmanagment.coreservice.attendance.specification;

import com.schoolmanagment.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.classsection.entity.ClassSectionHomeroom;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class AttendanceSpecification implements Specification<Attendance> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "dateOccurred",
            "createdAt",
            "id"
    );

    private final AttendanceFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Attendance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        UserContext.current().getCurrentExternalId()
                .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));

        applyCallerScope(root, query, cb, predicates);


        if (filterRequest.getEnrollmentId() != null) {
            predicates.add(cb.equal(
                    root.get("enrollmentTerm").get("enrollment").get("id"),
                    filterRequest.getEnrollmentId()));
        }

        if (filterRequest.getPenaltyTrigger() != null) {
            predicates.add(cb.equal(root.get("penaltyTrigger"), filterRequest.getPenaltyTrigger()));
        }

        if (filterRequest.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
        }

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(
                    root.get("enrollmentTerm").get("enrollment").get("student").get("id"),
                    filterRequest.getStudentId()));
        }

        if (filterRequest.getSourceModule() != null) {
            List<PenaltyTrigger> triggers = PenaltyTrigger.valuesFor(filterRequest.getSourceModule());
            predicates.add(root.get("penaltyTrigger").in(triggers));
        }

        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            applySorting(root, query, cb);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applyCallerScope(
            Root<Attendance> root,
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
        if (context.hasTeacherPolicy()) {
            context.getCurrentExternalId().ifPresentOrElse(
                    teacherId -> predicates.add(enrolledInTeacherHomeroom(root, query, cb, teacherId)),
                    () -> predicates.add(cb.disjunction()));
        }
    }

    /**
     * Enrollment.classSection matches an active homeroom assignment for this teacher.
     */
    private Predicate enrolledInTeacherHomeroom(
            Root<Attendance> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID teacherId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<ClassSectionHomeroom> homeroom = subquery.from(ClassSectionHomeroom.class);
        subquery.select(homeroom.get("id"));
        subquery.where(
                cb.equal(
                        homeroom.get("classSection"),
                        root.get("enrollmentTerm").get("enrollment").get("classSection")),
                cb.equal(homeroom.get("teacher").get("id"), teacherId),
                cb.isTrue(homeroom.get("active")),
                cb.isTrue(homeroom.get("classSection").get("active")),
                cb.isTrue(root.get("enrollmentTerm").get("enrollment").get("active"))
        );
        return cb.exists(subquery);
    }

    private Predicate linkedToEmergencyContact(
            Root<Attendance> root,
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

    private Path<UUID> studentId(Root<Attendance> root) {
        return root.get("enrollmentTerm").get("enrollment").get("student").get("id");
    }

    private void applySorting(Root<Attendance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "dateOccurred";
        }

        boolean ascending = "ASC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
