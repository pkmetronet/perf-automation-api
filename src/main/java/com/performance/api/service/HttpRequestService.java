package com.performance.api.service;

import com.performance.api.model.HttpRequest;
import com.performance.api.repository.HttpRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HttpRequestService {

    private final HttpRequestRepository httpRequestRepository;

    public HttpRequestService(HttpRequestRepository httpRequestRepository) {
        this.httpRequestRepository = httpRequestRepository;
    }

    public HttpRequest createHttpRequest(HttpRequest httpRequest) {
        return httpRequestRepository.save(httpRequest);
    }

    public List<HttpRequest> getAllHttpRequests() {
        return httpRequestRepository.findAll();
    }

    public Optional<HttpRequest> getHttpRequestById(Long id) {
        return httpRequestRepository.findById(id);
    }

    public List<HttpRequest> getHttpRequestsByThreadGroupId(Long threadGroupId) {
        return httpRequestRepository.findByThreadGroupIdOrderBySequenceOrderAsc(threadGroupId);
    }

    public HttpRequest updateHttpRequest(Long id, HttpRequest updatedDetails) {
        return httpRequestRepository.findById(id).map(existing -> {
            existing.setThreadGroupId(updatedDetails.getThreadGroupId());
            existing.setName(updatedDetails.getName());
            existing.setDescription(updatedDetails.getDescription());
            existing.setSequenceOrder(updatedDetails.getSequenceOrder());
            existing.setProtocol(updatedDetails.getProtocol());
            existing.setDomain(updatedDetails.getDomain());
            existing.setPort(updatedDetails.getPort());
            existing.setMethod(updatedDetails.getMethod());
            existing.setPath(updatedDetails.getPath());
            existing.setBodyData(updatedDetails.getBodyData());
            existing.setFollowRedirects(updatedDetails.getFollowRedirects());
            existing.setUseKeepalive(updatedDetails.getUseKeepalive());
            existing.setTimeoutConnect(updatedDetails.getTimeoutConnect());
            existing.setTimeoutResponse(updatedDetails.getTimeoutResponse());
            existing.setUpdatedBy(updatedDetails.getUpdatedBy());
            return httpRequestRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("HTTP Request not found with id: " + id));
    }

    public void deleteHttpRequest(Long id) {
        httpRequestRepository.deleteById(id);
    }
}