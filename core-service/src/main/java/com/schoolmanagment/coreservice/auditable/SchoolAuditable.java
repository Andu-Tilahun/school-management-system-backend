package com.schoolmanagment.coreservice.auditable;

import com.schoolmanagment.commonapplication.entity.Auditable;
import com.schoolmanagment.commonsecurity.util.UserContext;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.util.Optional;
import java.util.UUID;

@MappedSuperclass
public abstract class SchoolAuditable extends Auditable {

    @Column(name = "school_id", updatable = false, nullable = false)
    private UUID schoolId;

    @PrePersist
    private void fillSchoolIdFromLoggedInUser() {
        if (this.schoolId == null) {
            this.schoolId = Optional.ofNullable(UserContext.current())
                    .flatMap(UserContext::getCurrentExternalId)
                    .orElseThrow(() -> new IllegalStateException(
                            "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
        }
    }

    public UUID getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(UUID schoolId) {
        this.schoolId = schoolId;
    }
}
