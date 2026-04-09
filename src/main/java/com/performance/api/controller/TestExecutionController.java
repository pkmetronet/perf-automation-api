package com.performance.api.controller;

import com.performance.api.model.TestExecution;
import com.performance.api.service.TestExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test-executions")
public class TestExecutionController {

    private final TestExecutionService testExecutionService;

    public TestExecutionController(TestExecutionService testExecutionService) {
        this.testExecutionService = testExecutionService;
    }

    @PostMapping
    public ResponseEntity<TestExecution> createTestExecution(@RequestBody TestExecution testExecution) {
        TestExecution created = testExecutionService.createTestExecution(testExecution);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TestExecution>> getAllTestExecutions() {
        return ResponseEntity.ok(testExecutionService.getAllTestExecutions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestExecution> getTestExecutionById(@PathVariable Long id) {
        return testExecutionService.getTestExecutionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test-plan/{testPlanId}")
    public ResponseEntity<List<TestExecution>> getTestExecutionsByTestPlanId(@PathVariable Long testPlanId) {
        List<TestExecution> executions = testExecutionService.getTestExecutionsByTestPlanId(testPlanId);
        return ResponseEntity.ok(executions);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TestExecution> updateTestExecutionStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().build();
            }
            TestExecution updated = testExecutionService.updateTestExecutionStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestExecution(@PathVariable Long id) {
        testExecutionService.deleteTestExecution(id);
        return ResponseEntity.noContent().build();
    }
}