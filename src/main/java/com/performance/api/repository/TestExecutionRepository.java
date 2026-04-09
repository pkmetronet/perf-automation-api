package com.performance.api.repository;

import com.performance.api.model.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {
    
    // Fetch all executions for a specific Test Plan, ordered by newest first
    List<TestExecution> findByTestPlanIdOrderByStartTimeDesc(Long testPlanId);
}