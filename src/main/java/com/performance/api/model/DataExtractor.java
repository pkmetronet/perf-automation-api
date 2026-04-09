package com.performance.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "data_extractor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "http_request_id", nullable = false)
    private Long httpRequestId;

    @Column(nullable = false)
    private String name;

    @Column(name = "extractor_type", nullable = false)
    private String extractorType; // e.g., JSON_PATH, REGEX

    @Column(name = "variable_name", nullable = false)
    private String variableName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expression;

    @Column(name = "match_number")
    private Integer matchNumber = 1;

    @Column(name = "default_value")
    private String defaultValue;

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