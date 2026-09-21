package com.schoolmanagment.coreservice.penalty.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleFilterRequest;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class PenaltyRuleSpecification implements Specification<PenaltyRule> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "occurrenceNumber",
            "penaltyTrigger",
            "penaltyType",
            "createdAt",
            "id"
    );

    private final PenaltyRuleFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<PenaltyRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest.getPenaltyTrigger() != null) {
            predicates.add(cb.equal(root.get("penaltyTrigger"), filterRequest.getPenaltyTrigger()));
        }

        if (filterRequest.getSourceModule() != null) {
            List<PenaltyTrigger> triggers = PenaltyTrigger.valuesFor(filterRequest.getSourceModule());
            predicates.add(root.get("penaltyTrigger").in(triggers));
        }

        if (filterRequest.getPenaltyType() != null) {
            predicates.add(cb.equal(root.get("penaltyType"), filterRequest.getPenaltyType()));
        }

        if (filterRequest.getOccurrenceNumber() != null) {
            predicates.add(cb.equal(root.get("occurrenceNumber"), filterRequest.getOccurrenceNumber()));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String likeValue = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.like(cb.lower(root.get("penaltyTrigger").as(String.class)), likeValue));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<PenaltyRule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        if (sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy)) {
            sortBy = "occurrenceNumber";
        }

        boolean ascending = !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
