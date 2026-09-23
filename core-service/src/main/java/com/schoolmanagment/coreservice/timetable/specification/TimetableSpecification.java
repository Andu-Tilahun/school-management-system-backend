package com.schoolmanagment.coreservice.timetable.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.timetable.dto.TimetableFilterRequest;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import com.schoolmanagment.coreservice.timetable.enums.Day;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class TimetableSpecification implements Specification<Timetable> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "day",
            "period",
            "createdAt",
            "id"
    );

    private final TimetableFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<Timetable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        } else if (filterRequest.getSchoolId() != null) {
            predicates.add(cb.equal(root.get("schoolId"), filterRequest.getSchoolId()));
        }

        if (filterRequest.getClassSectionId() != null) {
            predicates.add(cb.equal(root.get("classSection").get("id"), filterRequest.getClassSectionId()));
        }

        if (filterRequest.getTeacherSubjectAssignmentId() != null) {
            predicates.add(cb.equal(
                    root.get("teacherSubjectAssignment").get("id"),
                    filterRequest.getTeacherSubjectAssignmentId()
            ));
        }

        if (filterRequest.getSubjectId() != null) {
            predicates.add(cb.equal(
                    root.get("teacherSubjectAssignment").get("subject").get("id"),
                    filterRequest.getSubjectId()
            ));
        }

        if (filterRequest.getTeacherId() != null) {
            predicates.add(cb.equal(
                    root.get("teacherSubjectAssignment").get("teacher").get("id"),
                    filterRequest.getTeacherId()
            ));
        }

        if (filterRequest.getDay() != null) {
            predicates.add(cb.equal(root.get("day"), filterRequest.getDay()));
        }

        if (filterRequest.getPeriod() != null) {
            predicates.add(cb.equal(root.get("period"), filterRequest.getPeriod()));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String likeValue = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            Join<Object, Object> classSection = root.join("classSection");
            Join<Object, Object> assignment = root.join("teacherSubjectAssignment");
            Join<Object, Object> subject = assignment.join("subject");
            Join<Object, Object> teacher = assignment.join("teacher");
            predicates.add(cb.or(
                    cb.like(cb.lower(classSection.get("name")), likeValue),
                    cb.like(cb.lower(subject.get("subjectName")), likeValue),
                    cb.like(cb.lower(subject.get("subjectCode")), likeValue),
                    cb.like(cb.lower(teacher.get("firstName")), likeValue),
                    cb.like(cb.lower(teacher.get("middleName")), likeValue),
                    cb.like(cb.lower(teacher.get("lastName")), likeValue)
            ));
        }

        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            applySorting(root, query, cb);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<Timetable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        boolean usingDefaultSort = sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy);
        if (usingDefaultSort) {
            query.orderBy(cb.asc(dayRank(root, cb)), cb.asc(root.get("period")));
            return;
        }

        boolean ascending = !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        Expression<?> sortExpression = "day".equals(sortBy) ? dayRank(root, cb) : root.get(sortBy);
        if (ascending) {
            query.orderBy(cb.asc(sortExpression));
        } else {
            query.orderBy(cb.desc(sortExpression));
        }
    }

    private Expression<Integer> dayRank(Root<Timetable> root, CriteriaBuilder cb) {
        return cb.<Integer>selectCase()
                .when(cb.equal(root.get("day"), Day.MONDAY), 1)
                .when(cb.equal(root.get("day"), Day.TUESDAY), 2)
                .when(cb.equal(root.get("day"), Day.WEDNESDAY), 3)
                .when(cb.equal(root.get("day"), Day.THURSDAY), 4)
                .when(cb.equal(root.get("day"), Day.FRIDAY), 5)
                .otherwise(6);
    }
}
