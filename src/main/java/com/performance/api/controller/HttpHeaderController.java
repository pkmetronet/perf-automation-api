package com.performance.api.controller;

import com.performance.api.model.HttpHeader;
import com.performance.api.service.HttpHeaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/http-headers")
public class HttpHeaderController {

    private final HttpHeaderService httpHeaderService;

    public HttpHeaderController(HttpHeaderService httpHeaderService) {
        this.httpHeaderService = httpHeaderService;
    }

    @PostMapping
    public ResponseEntity<?> createHttpHeader(@RequestBody HttpHeader httpHeader) {
        try {
            HttpHeader created = httpHeaderService.createHttpHeader(httpHeader);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<HttpHeader>> getAllHttpHeaders() {
        return ResponseEntity.ok(httpHeaderService.getAllHttpHeaders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpHeader> getHttpHeaderById(@PathVariable Long id) {
        return httpHeaderService.getHttpHeaderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/thread-group/{threadGroupId}")
    public ResponseEntity<List<HttpHeader>> getHttpHeadersByThreadGroupId(@PathVariable Long threadGroupId) {
        List<HttpHeader> headers = httpHeaderService.getHttpHeadersByThreadGroupId(threadGroupId);
        return ResponseEntity.ok(headers);
    }

    @GetMapping("/http-request/{httpRequestId}")
    public ResponseEntity<List<HttpHeader>> getHttpHeadersByHttpRequestId(@PathVariable Long httpRequestId) {
        List<HttpHeader> headers = httpHeaderService.getHttpHeadersByHttpRequestId(httpRequestId);
        return ResponseEntity.ok(headers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHttpHeader(@PathVariable Long id, @RequestBody HttpHeader httpHeader) {
        try {
            HttpHeader updated = httpHeaderService.updateHttpHeader(id, httpHeader);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHttpHeader(@PathVariable Long id) {
        httpHeaderService.deleteHttpHeader(id);
        return ResponseEntity.noContent().build();
    }
}