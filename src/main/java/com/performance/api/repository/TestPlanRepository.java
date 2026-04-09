package com.performance.api.repository;

import com.performance.api.model.TestPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestPlanRepository extends JpaRepository<TestPlan, Long> {
    // Spring Data JPA automatically provides basic CRUD methods like:
    // save(), findById(), findAll(), deleteById()
    
    // You can define custom queries here later if needed, e.g.:
    // List<TestPlan> findByCreatedBy(String createdBy);
}