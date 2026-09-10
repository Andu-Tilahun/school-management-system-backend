package com.schoolmanagment.coreservice.academicyear.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearFilterRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
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
public class AcademicYearSpecification implements Specification<AcademicYear> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "acYear",
            "semester",
            "createdAt",
            "id"
    );

    private final AcademicYearFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<AcademicYear> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("active"), filterRequest.getActive()));

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String likeValue = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.like(cb.lower(root.get("acYear")), likeValue));
        }

        if (filterRequest.getSemester() != null && !filterRequest.getSemester().isBlank()) {
            predicates.add(cb.equal(root.get("semester"), filterRequest.getSemester()));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<AcademicYear> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        boolean usingDefaultSort = sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy);
        if (usingDefaultSort) {
            sortBy = "acYear";
        }

        boolean ascending = !usingDefaultSort && !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
