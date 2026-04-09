package com.performance.api.service;

import com.performance.api.model.TestPlan;
import com.performance.api.repository.TestPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestPlanService {

    private final TestPlanRepository testPlanRepository;

    public TestPlanService(TestPlanRepository testPlanRepository) {
        this.testPlanRepository = testPlanRepository;
    }

    public TestPlan createTestPlan(TestPlan testPlan) {
        return testPlanRepository.save(testPlan);
    }

    public List<TestPlan> getAllTestPlans() {
        return testPlanRepository.findAll();
    }

    public Optional<TestPlan> getTestPlanById(Long id) {
        return testPlanRepository.findById(id);
    }

    public TestPlan updateTestPlan(Long id, TestPlan updatedPlanDetails) {
        return testPlanRepository.findById(id).map(existingPlan -> {
            existingPlan.setName(updatedPlanDetails.getName());
            existingPlan.setDescription(updatedPlanDetails.getDescription());
            existingPlan.setUpdatedBy(updatedPlanDetails.getUpdatedBy());
            // Updated_at is handled automatically by @UpdateTimestamp in the Entity
            return testPlanRepository.save(existingPlan);
        }).orElseThrow(() -> new RuntimeException("Test Plan not found with id: " + id));
    }

    public void deleteTestPlan(Long id) {
        testPlanRepository.deleteById(id);
    }
}