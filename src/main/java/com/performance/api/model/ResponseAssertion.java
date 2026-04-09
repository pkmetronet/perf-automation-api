package com.performance.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "assertion")
@Data
public class ResponseAssertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "http_request_id", nullable = false)
    private Long httpRequestId;

    @Column(nullable = false)
    private String name;

    @Column(name = "assertion_type", nullable = false)
    // e.g., STATUS_CODE, CONTAINS_TEXT, JSON_PATH
    private String type;

    @Column(name = "field_to_test", nullable = false, length = 50)
    private String fieldToTest;

    @Column(name = "match_rule", nullable = false, length = 50)
    // For text assertions: SUBSTRING, MATCHES, NOT_CONTAINS
    private String matchingRule;

    @Column(name = "expected_value", nullable = false, columnDefinition = "TEXT")
    // e.g., "200", "Success", "$.data.id"
    private String expectedValue;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}