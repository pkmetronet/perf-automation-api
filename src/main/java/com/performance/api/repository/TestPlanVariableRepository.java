package com.performance.api.repository;

import com.performance.api.model.TestPlanVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPlanVariableRepository extends JpaRepository<TestPlanVariable, Long> {
    
    // Custom query to fetch all variables linked to a specific Test Plan
    List<TestPlanVariable> findByTestPlanId(Long testPlanId);
}