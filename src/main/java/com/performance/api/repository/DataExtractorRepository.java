package com.performance.api.repository;

import com.performance.api.model.DataExtractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataExtractorRepository extends JpaRepository<DataExtractor, Long> {
    
    // Custom query to fetch all extractors linked to a specific HTTP Request
    List<DataExtractor> findByHttpRequestId(Long httpRequestId);
}