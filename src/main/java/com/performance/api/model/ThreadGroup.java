package com.performance.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Types;
import java.time.ZonedDateTime;

@Entity
@Table(name = "thread_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_plan_id", nullable = false)
    private Long testPlanId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(Types.ARRAY)
    @Column(name = "request_sequence", columnDefinition = "bigint[]")
    private Long[] requestSequence;

    @Column(name = "num_threads", nullable = false)
    private Integer numThreads = 1;

    @Column(name = "ramp_up_time", nullable = false)
    private Integer rampUpTime = 1;

    @Column(name = "loop_count", nullable = false)
    private Integer loopCount = 1;

    @Column(name = "scheduler_enabled")
    private Boolean schedulerEnabled = false;

    private Integer duration;

    @Column(name = "startup_delay")
    private Integer startupDelay;

    @Column(name = "on_sample_error")
    private String onSampleError = "CONTINUE";

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