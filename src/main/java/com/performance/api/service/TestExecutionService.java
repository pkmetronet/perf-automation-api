package com.performance.api.service;

import com.performance.api.model.TestExecution;
import com.performance.api.repository.TestExecutionRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TestExecutionService {

    private final TestExecutionRepository testExecutionRepository;
    private final String METRICS_DIRECTORY = "api/metrics";

    public TestExecutionService(TestExecutionRepository testExecutionRepository) {
        this.testExecutionRepository = testExecutionRepository;
    }

    public TestExecution createTestExecution(TestExecution testExecution) {
        // 1. Set initial start time and save to generate the ID
        testExecution.setStartTime(ZonedDateTime.now());
        TestExecution savedExecution = testExecutionRepository.save(testExecution);

        // 2. Construct the JTL file path using the generated ID
        String jtlFilePath = METRICS_DIRECTORY + "/" + savedExecution.getId() + ".jtl";
        savedExecution.setS3KeyResultTree(jtlFilePath);

        // 3. Ensure the physical directory exists on the local system
        try {
            Path path = Paths.get(METRICS_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            // Future Engine step: Initialize/Create the empty .jtl file here if needed
        } catch (Exception e) {
            throw new RuntimeException("Failed to create metrics directory: " + e.getMessage());
        }

        // 4. Update the execution record with the S3 Key
        return testExecutionRepository.save(savedExecution);
    }

    public List<TestExecution> getAllTestExecutions() {
        return testExecutionRepository.findAll();
    }

    public Optional<TestExecution> getTestExecutionById(Long id) {
        return testExecutionRepository.findById(id);
    }

    public List<TestExecution> getTestExecutionsByTestPlanId(Long testPlanId) {
        return testExecutionRepository.findByTestPlanIdOrderByStartTimeDesc(testPlanId);
    }

    public TestExecution updateTestExecutionStatus(Long id, String status) {
        return testExecutionRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            if ("COMPLETED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
                existing.setEndTime(ZonedDateTime.now());
            }
            return testExecutionRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Test Execution not found with id: " + id));
    }

    public void deleteTestExecution(Long id) {
        testExecutionRepository.deleteById(id);
    }
}