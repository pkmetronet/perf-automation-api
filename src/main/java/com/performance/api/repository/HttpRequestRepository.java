package com.performance.api.repository;

import com.performance.api.model.HttpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HttpRequestRepository extends JpaRepository<HttpRequest, Long> {
    
    // Fetch all requests for a Thread Group (sorting is done in service based on requestSequence)
    List<HttpRequest> findByThreadGroupId(Long threadGroupId);
}