package com.performance.api.controller;

import com.performance.api.model.DataExtractor;
import com.performance.api.service.DataExtractorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data-extractors")
public class DataExtractorController {

    private final DataExtractorService dataExtractorService;

    public DataExtractorController(DataExtractorService dataExtractorService) {
        this.dataExtractorService = dataExtractorService;
    }

    @PostMapping
    public ResponseEntity<DataExtractor> createDataExtractor(@RequestBody DataExtractor dataExtractor) {
        DataExtractor created = dataExtractorService.createDataExtractor(dataExtractor);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DataExtractor>> getAllDataExtractors() {
        return ResponseEntity.ok(dataExtractorService.getAllDataExtractors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataExtractor> getDataExtractorById(@PathVariable Long id) {
        return dataExtractorService.getDataExtractorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/http-request/{httpRequestId}")
    public ResponseEntity<List<DataExtractor>> getDataExtractorsByHttpRequestId(@PathVariable Long httpRequestId) {
        List<DataExtractor> extractors = dataExtractorService.getDataExtractorsByHttpRequestId(httpRequestId);
        return ResponseEntity.ok(extractors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataExtractor> updateDataExtractor(@PathVariable Long id, @RequestBody DataExtractor dataExtractor) {
        try {
            DataExtractor updated = dataExtractorService.updateDataExtractor(id, dataExtractor);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataExtractor(@PathVariable Long id) {
        dataExtractorService.deleteDataExtractor(id);
        return ResponseEntity.noContent().build();
    }
}