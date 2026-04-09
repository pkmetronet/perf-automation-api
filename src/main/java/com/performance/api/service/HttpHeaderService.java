package com.performance.api.service;

import com.performance.api.model.HttpHeader;
import com.performance.api.repository.HttpHeaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HttpHeaderService {

    private final HttpHeaderRepository httpHeaderRepository;

    public HttpHeaderService(HttpHeaderRepository httpHeaderRepository) {
        this.httpHeaderRepository = httpHeaderRepository;
    }

    private void validateHeaderAttachment(HttpHeader header) {
        boolean hasThreadGroup = header.getThreadGroupId() != null;
        boolean hasHttpRequest = header.getHttpRequestId() != null;
        
        if ((hasThreadGroup && hasHttpRequest) || (!hasThreadGroup && !hasHttpRequest)) {
            throw new IllegalArgumentException("An HTTP Header must be attached to EXACTLY ONE parent (either Thread Group OR HTTP Request).");
        }
    }

    public HttpHeader createHttpHeader(HttpHeader httpHeader) {
        validateHeaderAttachment(httpHeader);
        return httpHeaderRepository.save(httpHeader);
    }

    public List<HttpHeader> getAllHttpHeaders() {
        return httpHeaderRepository.findAll();
    }

    public Optional<HttpHeader> getHttpHeaderById(Long id) {
        return httpHeaderRepository.findById(id);
    }

    public List<HttpHeader> getHttpHeadersByThreadGroupId(Long threadGroupId) {
        return httpHeaderRepository.findByThreadGroupId(threadGroupId);
    }

    public List<HttpHeader> getHttpHeadersByHttpRequestId(Long httpRequestId) {
        return httpHeaderRepository.findByHttpRequestId(httpRequestId);
    }

    public HttpHeader updateHttpHeader(Long id, HttpHeader updatedDetails) {
        validateHeaderAttachment(updatedDetails);
        
        return httpHeaderRepository.findById(id).map(existing -> {
            existing.setThreadGroupId(updatedDetails.getThreadGroupId());
            existing.setHttpRequestId(updatedDetails.getHttpRequestId());
            existing.setName(updatedDetails.getName());
            existing.setValue(updatedDetails.getValue());
            existing.setUpdatedBy(updatedDetails.getUpdatedBy());
            return httpHeaderRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("HTTP Header not found with id: " + id));
    }

    public void deleteHttpHeader(Long id) {
        httpHeaderRepository.deleteById(id);
    }
}