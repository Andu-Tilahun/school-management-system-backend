package com.schoolmanagment.coreservice.student.specification;

import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.entity.Student;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class StudentSpecification implements Specification<Student> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "firstName",
            "lastName",
            "age",
            "createdAt",
            "id"
    );

    private final StudentFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        if (filterRequest.getSchoolId() != null) {
            predicates.add(cb.equal(root.get("schoolId"), filterRequest.getSchoolId()));
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
