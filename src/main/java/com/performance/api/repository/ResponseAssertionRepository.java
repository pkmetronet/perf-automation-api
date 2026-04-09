package com.performance.api.repository;

import com.performance.api.model.ResponseAssertion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseAssertionRepository extends JpaRepository<ResponseAssertion, Long> {
    List<ResponseAssertion> findByHttpRequestId(Long httpRequestId);
}