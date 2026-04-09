package com.performance.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "test_execution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_plan_id", nullable = false)
    private Long testPlanId;

    @Column(name = "thread_group_id")
    private Long threadGroupId;

    @Column(nullable = false, length = 20)
    private String status = "RUNNING"; // PENDING, RUNNING, COMPLETED, FAILED

    @Column(name = "started_at")
    private ZonedDateTime startTime;

    @Column(name = "ended_at")
    private ZonedDateTime endTime;

    @Column(name = "trigger_source", length = 100)
    private String triggerSource;

    @Column(name = "executed_by")
    private String executedBy;

    @Column(name = "s3_key_result_tree", columnDefinition = "TEXT")
    private String s3KeyResultTree;

    @Column(name = "s3_key_aggregated_report", columnDefinition = "TEXT")
    private String s3KeyAggregatedReport;
}