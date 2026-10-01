package com.schoolmanagment.coreservice.school.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.school.dto.SchoolFilterRequest;
import com.schoolmanagment.coreservice.school.entity.School;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class SchoolSpecification implements Specification<School> {

    private final SchoolFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<School> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (UserContext.current().hasTenantManager()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("tenant").get("id"), externalId)));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
