package com.schoolmanagment.coreservice.offencerecord.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordFilterRequest;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class OffenceRecordSpecification implements Specification<OffenceRecord> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "dateOccurred",
            "createdAt",
            "id"
    );

    private final OffenceRecordFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<OffenceRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (filterRequest.getActive() != null) {
            predicates.add(cb.equal(root.get("active"), filterRequest.getActive()));
        }
        applyCallerScope(root, query, cb, predicates);


        if (filterRequest.getEnrollmentId() != null) {
            predicates.add(cb.equal(
                    root.get("enrollmentTerm").get("enrollment").get("id"),
                    filterRequest.getEnrollmentId()));
        }

        if (filterRequest.getPenaltyTrigger() != null) {
            predicates.add(cb.equal(root.get("penaltyTrigger"), filterRequest.getPenaltyTrigger()));
        }

        if (filterRequest.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filterRequest.getStatus()));
        }

        if (filterRequest.getStudentId() != null) {
            predicates.add(cb.equal(
                    root.get("enrollmentTerm").get("enrollment").get("student").get("id"),
                    filterRequest.getStudentId()));
        }

        if (filterRequest.getSourceModule() != null) {
            List<PenaltyTrigger> triggers = PenaltyTrigger.valuesFor(filterRequest.getSourceModule());
            predicates.add(root.get("penaltyTrigger").in(triggers));
        }

        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            applySorting(root, query, cb);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applyCallerScope(
            Root<OffenceRecord> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            ArrayList<Predicate> predicates
    ) {
        UserContext context = UserContext.current();
        if (context.hasStudentPolicy()) {
            context.getCurrentExternalId().ifPresentOrElse(
                    studentId -> predicates.add(cb.equal(studentId(root), studentId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        if (context.hasEmergencyContactPolicy()) {
            context.getCurrentExternalId().ifPresentOrElse(
                    contactId -> predicates.add(linkedToEmergencyContact(root, query, cb, contactId)),
                    () -> predicates.add(cb.disjunction()));
            return;
        }
        context.getCurrentExternalId()
                .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
    }

    private Predicate linkedToEmergencyContact(
            Root<OffenceRecord> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            UUID emergencyContactId
    ) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<StudentEmergencyContact> link = subquery.from(StudentEmergencyContact.class);
        subquery.select(link.get("id"));
        subquery.where(
                cb.equal(link.get("student"), root.get("enrollmentTerm").get("enrollment").get("student")),
                cb.equal(link.get("emergencyContact").get("id"), emergencyContactId),
                cb.isTrue(link.get("active")),
                cb.isTrue(link.get("emergencyContact").get("active"))
        );
        return cb.exists(subquery);
    }

    private Path<UUID> studentId(Root<OffenceRecord> root) {
        return root.get("enrollmentTerm").get("enrollment").get("student").get("id");
    }

    private void applySorting(Root<OffenceRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
