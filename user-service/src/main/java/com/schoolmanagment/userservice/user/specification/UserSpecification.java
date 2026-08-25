package com.schoolmanagment.userservice.user.specification;

import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.user.dto.UserFilterRequest;
import com.schoolmanagment.userservice.user.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id",
            "username",
            "email",
            "firstName",
            "lastName",
            "middleName",
            "gender",
            "enabled",
            "accountNonLocked",
            "createdAt",
            "updatedAt"
    );

    private final UserFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (filterRequest.getPolicyNames() != null && !filterRequest.getPolicyNames().isEmpty()) {
            List<String> nameStrings = filterRequest.getPolicyNames().stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            if (!nameStrings.isEmpty()) {
                Join<User, Policy> directPolicies = root.join("policies", JoinType.LEFT);
                Join<User, Group> groupsJoin = root.join("groups", JoinType.LEFT);
                Join<Group, Policy> groupPolicies = groupsJoin.join("policies", JoinType.LEFT);
                Predicate direct = directPolicies.get("name").in(nameStrings);
                Predicate viaGroup = groupPolicies.get("name").in(nameStrings);
                predicates.add(cb.or(direct, viaGroup));
                query.distinct(true);
            }
        }

        if (filterRequest.getGenders() != null && !filterRequest.getGenders().isEmpty()) {
            List<Predicate> genderPredicates = new ArrayList<>();
            for (String gender : filterRequest.getGenders()) {
                genderPredicates.add(
                        cb.equal(cb.lower(root.get("gender")), gender.toLowerCase(Locale.ROOT)));
            }
            if (!genderPredicates.isEmpty()) {
                predicates.add(cb.or(genderPredicates.toArray(new Predicate[0])));
            }
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String searchText = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            List<Predicate> searchPredicates = new ArrayList<>();
            searchPredicates.add(cb.like(cb.lower(root.get("firstName")), searchText));
            searchPredicates.add(cb.like(cb.lower(root.get("lastName")), searchText));
            searchPredicates.add(cb.like(cb.lower(root.get("email")), searchText));
            searchPredicates.add(cb.like(cb.lower(root.get("username")), searchText));
            predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank()) {
            query.orderBy(cb.desc(root.get("id")));
            return;
        }
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        boolean ascending = !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
