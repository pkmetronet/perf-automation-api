package com.performance.api.service;

import com.performance.api.model.HttpRequest;
import com.performance.api.model.ThreadGroup;
import com.performance.api.repository.HttpRequestRepository;
import com.performance.api.repository.ThreadGroupRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HttpRequestService {

    private final HttpRequestRepository httpRequestRepository;
    private final ThreadGroupRepository threadGroupRepository;

    public HttpRequestService(HttpRequestRepository httpRequestRepository,
                              ThreadGroupRepository threadGroupRepository) {
        this.httpRequestRepository = httpRequestRepository;
        this.threadGroupRepository = threadGroupRepository;
    }

    public HttpRequest createHttpRequest(HttpRequest httpRequest) {
        HttpRequest savedRequest = httpRequestRepository.save(httpRequest);
        return savedRequest;
    }

    public List<HttpRequest> getAllHttpRequests() {
        return httpRequestRepository.findAll();
    }

    public Optional<HttpRequest> getHttpRequestById(Long id) {
        return httpRequestRepository.findById(id);
    }

    public List<HttpRequest> getHttpRequestsByThreadGroupId(Long threadGroupId) {
        // Fetch the ThreadGroup to get the requestSequence
        Optional<ThreadGroup> threadGroupOpt = threadGroupRepository.findById(threadGroupId);
        
        if (threadGroupOpt.isEmpty()) {
            return Collections.emptyList();
        }
        
        ThreadGroup threadGroup = threadGroupOpt.get();
        Long[] requestSequence = threadGroup.getRequestSequence();
        
        // Fetch all HttpRequests for the given threadGroupId
        List<HttpRequest> httpRequests = httpRequestRepository.findByThreadGroupId(threadGroupId);
        
        // If no requestSequence is defined, return as-is (ordered by insertion)
        if (requestSequence == null || requestSequence.length == 0) {
            return httpRequests;
        }
        
        // Create a map for quick lookup by request ID
        Map<Long, HttpRequest> requestMap = new HashMap<>();
        for (HttpRequest request : httpRequests) {
            requestMap.put(request.getId(), request);
        }
        
        // Build the sorted list based on requestSequence order
        List<HttpRequest> sortedRequests = new ArrayList<>();
        for (Long requestId : requestSequence) {
            HttpRequest request = requestMap.get(requestId);
            if (request != null) {
                sortedRequests.add(request);
            }
        }
        
        // Add any requests not in the sequence (fallback)
        for (HttpRequest request : httpRequests) {
            if (!sortedRequests.contains(request)) {
                sortedRequests.add(request);
            }
        }
        
        return sortedRequests;
    }

    public HttpRequest updateHttpRequest(Long id, HttpRequest updatedDetails) {
        return httpRequestRepository.findById(id).map(existing -> {
            existing.setThreadGroupId(updatedDetails.getThreadGroupId());
            existing.setName(updatedDetails.getName());
            existing.setDescription(updatedDetails.getDescription());
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