package com.performance.api.repository;

import com.performance.api.model.HttpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HttpRequestRepository extends JpaRepository<HttpRequest, Long> {
    
    // Custom query to fetch all requests for a Thread Group, strictly ordered by execution sequence
    List<HttpRequest> findByThreadGroupIdOrderBySequenceOrderAsc(Long threadGroupId);
}