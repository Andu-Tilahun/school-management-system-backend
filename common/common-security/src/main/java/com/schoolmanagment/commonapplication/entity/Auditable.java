package com.schoolmanagment.commonapplication.entity;

import com.schoolmanagment.commonsecurity.util.UserContext;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_by_name", updatable = false)
    private String createdByName;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @PrePersist
    void snapshotAuditorNamesOnCreate() {
        String username = currentUserName();
        if (username == null) {
            return;
        }
        if (this.createdByName == null) {
            this.createdByName = username;
        }
        this.updatedByName = username;
    }

    @PreUpdate
    void snapshotAuditorNameOnUpdate() {
        String username = currentUserName();
        if (username != null) {
            this.updatedByName = username;
        }
    }

    private static String currentUserName() {
        UserContext ctx = UserContext.current();
        if (ctx == null) {
            return null;
        }
        return ctx.getCurrentUserName();
    }
}
