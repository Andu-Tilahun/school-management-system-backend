package com.schoolmanagment.coreservice.roomsection.specification;

import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionFilterRequest;
import com.schoolmanagment.coreservice.roomsection.entity.RoomSection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class RoomSectionSpecification implements Specification<RoomSection> {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "createdAt",
            "id"
    );

    private final RoomSectionFilterRequest filterRequest;

    @Override
    public Predicate toPredicate(Root<RoomSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(root.get("active")));

        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId()
                    .ifPresent(externalId -> predicates.add(cb.equal(root.get("schoolId"), externalId)));
        }

        if (filterRequest.getClassSectionId() != null) {
            predicates.add(cb.equal(root.get("classSection").get("id"), filterRequest.getClassSectionId()));
        }

        if (filterRequest.getRoomId() != null) {
            predicates.add(cb.equal(root.get("room").get("id"), filterRequest.getRoomId()));
        }

        if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().isBlank()) {
            String likeValue = "%" + filterRequest.getSearchText().toLowerCase(Locale.ROOT) + "%";
            Join<Object, Object> room = root.join("room");
            predicates.add(cb.like(cb.lower(room.get("roomNumber")), likeValue));
        }

        applySorting(root, query, cb);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(Root<RoomSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String sortBy = filterRequest.getSortBy();
        boolean usingDefaultSort = sortBy == null || sortBy.isBlank() || !SORTABLE_FIELDS.contains(sortBy);
        if (usingDefaultSort) {
            sortBy = "createdAt";
        }

        boolean ascending = !usingDefaultSort && !"DESC".equalsIgnoreCase(filterRequest.getSortDirection());
        if (ascending) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
