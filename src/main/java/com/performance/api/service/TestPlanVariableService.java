package com.performance.api.service;

import com.performance.api.model.TestPlanVariable;
import com.performance.api.repository.TestPlanVariableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestPlanVariableService {

    private final TestPlanVariableRepository testPlanVariableRepository;

    public TestPlanVariableService(TestPlanVariableRepository testPlanVariableRepository) {
        this.testPlanVariableRepository = testPlanVariableRepository;
    }

    public TestPlanVariable createTestPlanVariable(TestPlanVariable testPlanVariable) {
        // Because of the database UNIQUE constraint on (test_plan_id, name),
        // attempting to save a duplicate variable name for the same test plan will throw a DataIntegrityViolationException
        return testPlanVariableRepository.save(testPlanVariable);
    }

    public List<TestPlanVariable> getAllTestPlanVariables() {
        return testPlanVariableRepository.findAll();
    }

    public Optional<TestPlanVariable> getTestPlanVariableById(Long id) {
        return testPlanVariableRepository.findById(id);
    }

    public List<TestPlanVariable> getTestPlanVariablesByTestPlanId(Long testPlanId) {
        return testPlanVariableRepository.findByTestPlanId(testPlanId);
    }

    public TestPlanVariable updateTestPlanVariable(Long id, TestPlanVariable updatedDetails) {
        return testPlanVariableRepository.findById(id).map(existing -> {
            existing.setTestPlanId(updatedDetails.getTestPlanId());
            existing.setName(updatedDetails.getName());
            existing.setValue(updatedDetails.getValue());
            existing.setDescription(updatedDetails.getDescription());
            existing.setUpdatedBy(updatedDetails.getUpdatedBy());
            return testPlanVariableRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Test Plan Variable not found with id: " + id));
    }

    public void deleteTestPlanVariable(Long id) {
        testPlanVariableRepository.deleteById(id);
    }
}