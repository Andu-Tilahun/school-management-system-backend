package com.schoolmanagment.coreservice.student.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public class EnrollmentSpecification implements Specification<Enrollment> {

    private final UUID studentId;
    private final EnrollmentFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Enrollment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));
        predicates.add(cb.equal(root.get("student").get("id"), studentId));

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest != null) {
            if (filterRequest.getClassSectionId() != null) {
                predicates.add(cb.equal(root.get("classSection").get("id"), filterRequest.getClassSectionId()));
            }
            if (filterRequest.getAcademicYearId() != null) {
                predicates.add(cb.equal(root.get("academicYear").get("id"), filterRequest.getAcademicYearId()));
            }
            if (filterRequest.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
            }
        }

        query.orderBy(cb.desc(root.get("createdAt")));

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
