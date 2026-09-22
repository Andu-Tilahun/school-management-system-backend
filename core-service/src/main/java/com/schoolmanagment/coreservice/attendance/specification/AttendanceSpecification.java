package com.schoolmanagment.coreservice.attendance.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest.getEnrollmentId() != null) {
            predicates.add(cb.equal(root.get("enrollment").get("id"), filterRequest.getEnrollmentId()));
        }

        if (filterRequest.getPenaltyTrigger() != null) {
            predicates.add(cb.equal(root.get("penaltyTrigger"), filterRequest.getPenaltyTrigger()));
        }

        if (filterRequest.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
        }

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(root.get("enrollment").get("student").get("id"), filterRequest.getStudentId()));
        }

        if (filterRequest.getSourceModule() != null) {
            List<PenaltyTrigger> triggers = PenaltyTrigger.valuesFor(filterRequest.getSourceModule());
            predicates.add(root.get("penaltyTrigger").in(triggers));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
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
