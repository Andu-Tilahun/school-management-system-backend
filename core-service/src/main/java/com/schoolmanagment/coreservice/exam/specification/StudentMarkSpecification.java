package com.schoolmanagment.coreservice.exam.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Set;

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

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest.getEnrollmentTermId() != null) {
            predicates.add(cb.equal(root.get("enrollmentTerm").get("id"), filterRequest.getEnrollmentTermId()));
        }

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(
                    root.get("enrollmentTerm").get("enrollment").get("student").get("id"),
                    filterRequest.getStudentId()
            ));
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

        if (!isCountQuery(query)) {
            applySorting(root, query, cb);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> resultType = query.getResultType();
        return resultType == Long.class || resultType == long.class;
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
