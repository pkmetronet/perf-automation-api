package com.performance.api.controller;

import com.performance.api.model.HttpRequest;
import com.performance.api.service.HttpRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/http-requests")
public class HttpRequestController {

    private final HttpRequestService httpRequestService;

    public HttpRequestController(HttpRequestService httpRequestService) {
        this.httpRequestService = httpRequestService;
    }

    @PostMapping
    public ResponseEntity<HttpRequest> createHttpRequest(@RequestBody HttpRequest httpRequest) {
        HttpRequest created = httpRequestService.createHttpRequest(httpRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HttpRequest>> getAllHttpRequests() {
        return ResponseEntity.ok(httpRequestService.getAllHttpRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpRequest> getHttpRequestById(@PathVariable Long id) {
        return httpRequestService.getHttpRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to get all HTTP Requests for a specific Thread Group, ordered by sequence
    @GetMapping("/thread-group/{threadGroupId}")
    public ResponseEntity<List<HttpRequest>> getHttpRequestsByThreadGroupId(@PathVariable Long threadGroupId) {
        List<HttpRequest> requests = httpRequestService.getHttpRequestsByThreadGroupId(threadGroupId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpRequest> updateHttpRequest(@PathVariable Long id, @RequestBody HttpRequest httpRequest) {
        try {
            HttpRequest updated = httpRequestService.updateHttpRequest(id, httpRequest);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHttpRequest(@PathVariable Long id) {
        httpRequestService.deleteHttpRequest(id);
        return ResponseEntity.noContent().build();
    }
}