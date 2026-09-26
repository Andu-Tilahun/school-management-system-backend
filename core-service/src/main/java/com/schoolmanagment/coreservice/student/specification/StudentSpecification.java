package com.schoolmanagment.coreservice.student.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class StudentSpecification implements Specification<Student> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "firstName",
            "lastName",
            "birthDate",
            "createdAt",
            "id"
    );

    private final StudentFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        if (UserContext.current().hasSchoolAdminPolicy() || UserContext.current().hasStudentPolicy() ||
                UserContext.current().hasEmergencyContactPolicy()) {

            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (UserContext.current().hasStudentPolicy()) {

            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("id"), externalId)));
        }

        if (UserContext.current().hasEmergencyContactPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(linkedToEmergencyContact(root, query, cb, externalId)));
        }

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(root.get("id"), filterRequest.getStudentId()));
        }

        if (filterRequest.getClassSectionId() != null) {
            predicates.add(enrolledInClassSection(root, query, cb, filterRequest.getClassSectionId()));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String likeValue = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("firstName")), likeValue),
                    cb.like(cb.lower(root.get("lastName")), likeValue),
                    cb.like(cb.lower(root.get("middleName")), likeValue),
                    cb.like(cb.lower(root.get("mobileNumber")), likeValue)
            ));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate enrolledInClassSection(
            Root<Student> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID classSectionId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<Enrollment> enrollment = subquery.from(Enrollment.class);
        subquery.select(enrollment.get("id"));
        subquery.where(
                cb.equal(enrollment.get("student"), root),
                cb.equal(enrollment.get("classSection").get("id"), classSectionId),
                cb.isTrue(enrollment.get("active")),
                cb.equal(enrollment.get("status"), EnrollmentStatus.ACTIVE)
        );
        return cb.exists(subquery);
    }

    private Predicate linkedToEmergencyContact(
            Root<Student> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID emergencyContactId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<StudentEmergencyContact> link = subquery.from(StudentEmergencyContact.class);
        subquery.select(link.get("id"));
        subquery.where(
                cb.equal(link.get("student"), root),
                cb.equal(link.get("emergencyContact").get("id"), emergencyContactId),
                cb.isTrue(link.get("active"))
        );
        return cb.exists(subquery);
    }

    private void applySorting(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "lastName";
        }

        boolean ascending = !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
