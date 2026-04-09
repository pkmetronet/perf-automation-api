package com.performance.api.controller;

import com.performance.api.model.TestPlan;
import com.performance.api.service.TestPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-plans")
public class TestPlanController {

    private final TestPlanService testPlanService;

    public TestPlanController(TestPlanService testPlanService) {
        this.testPlanService = testPlanService;
    }

    @PostMapping
    public ResponseEntity<TestPlan> createTestPlan(@RequestBody TestPlan testPlan) {
        TestPlan created = testPlanService.createTestPlan(testPlan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TestPlan>> getAllTestPlans() {
        return ResponseEntity.ok(testPlanService.getAllTestPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestPlan> getTestPlanById(@PathVariable Long id) {
        return testPlanService.getTestPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestPlan> updateTestPlan(@PathVariable Long id, @RequestBody TestPlan testPlan) {
        try {
            TestPlan updated = testPlanService.updateTestPlan(id, testPlan);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestPlan(@PathVariable Long id) {
        testPlanService.deleteTestPlan(id);
        return ResponseEntity.noContent().build();
    }
}