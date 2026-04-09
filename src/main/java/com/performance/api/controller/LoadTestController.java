package com.performance.api.controller;

import com.performance.api.model.TestExecution;
import com.performance.api.service.LoadTestEngineService;
import com.performance.api.service.TestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/load-test")
public class LoadTestController {

    @Autowired
    private LoadTestEngineService engineService;

    @Autowired
    private TestExecutionService executionService;

    /**
     * Triggers a performance test execution for a specific test plan.
     */
    @PostMapping("/run/{testPlanId}")
    public ResponseEntity<?> runTest(@PathVariable Long testPlanId) {

        // Create a NEW execution record to track progress and link to results
        TestExecution newExecution = new TestExecution();
        newExecution.setTestPlanId(testPlanId);
        newExecution.setStatus("IN_PROGRESS");
        
        TestExecution execution = executionService.createTestExecution(newExecution);        

        // Start the JMeter DSL engine in a background thread
        engineService.executeTestPlanAsync(execution.getId(), testPlanId);
        
        return ResponseEntity.ok(Map.of(
            "message", "Test execution initiated successfully",
            "executionId", execution.getId(),
            "status", "IN_PROGRESS"
        ));
    }

    /**
     * Retrieves the current status and details of a test execution.
     */
    @GetMapping("/status/{executionId}")
    public ResponseEntity<?> getStatus(@PathVariable Long executionId) {
        return executionService.getTestExecutionById(executionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}