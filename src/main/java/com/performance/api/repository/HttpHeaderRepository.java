package com.performance.api.repository;

import com.performance.api.model.HttpHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HttpHeaderRepository extends JpaRepository<HttpHeader, Long> {
    
    // Fetch headers applied globally to a Thread Group
    List<HttpHeader> findByThreadGroupId(Long threadGroupId);

    // Fetch headers applied specifically to a single HTTP Request
    List<HttpHeader> findByHttpRequestId(Long httpRequestId);
}