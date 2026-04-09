package com.performance.api.repository;

import com.performance.api.model.ThreadGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadGroupRepository extends JpaRepository<ThreadGroup, Long> {
    
    // Custom query method to easily fetch all thread groups for a specific test plan
    List<ThreadGroup> findByTestPlanId(Long testPlanId);
}