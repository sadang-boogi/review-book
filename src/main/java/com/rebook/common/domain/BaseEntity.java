package com.rebook.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.ZonedDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false, columnDefinition = "datetime(6) default now()")
    private Instant createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "datetime(6) default now() on update now()")
    private Instant updatedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
