package com.schoolmanagment.userservice.permission.specification;

import com.schoolmanagment.userservice.permission.dto.PermissionFilterRequest;
import com.schoolmanagment.userservice.permission.entity.Permission;
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
public class PermissionSpecification implements Specification<Permission> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id",
            "name",
            "description",
            "createdAt"
    );

    private final PermissionFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (filterRequest.getResourceId() != null) {
            predicates.add(cb.equal(root.get("resource").get("id"), filterRequest.getResourceId()));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String like = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.like(cb.lower(root.get("name")), like));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "name";
        }
        boolean ascending = !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
