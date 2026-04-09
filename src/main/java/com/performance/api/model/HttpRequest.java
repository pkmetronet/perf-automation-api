package com.performance.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "http_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thread_group_id", nullable = false)
    private Long threadGroupId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(length = 10)
    private String protocol = "https";

    @Column(nullable = false)
    private String domain;

    private Integer port;

    @Column(nullable = false, length = 15)
    private String method = "GET";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "body_data", columnDefinition = "TEXT")
    private String bodyData;

    @Column(name = "follow_redirects")
    private Boolean followRedirects = true;

    @Column(name = "use_keepalive")
    private Boolean useKeepalive = true;

    @Column(name = "timeout_connect")
    private Integer timeoutConnect;

    @Column(name = "timeout_response")
    private Integer timeoutResponse;

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