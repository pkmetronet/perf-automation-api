package com.performance.api.service;

import com.performance.api.model.DataExtractor;
import com.performance.api.repository.DataExtractorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataExtractorService {

    private final DataExtractorRepository dataExtractorRepository;

    public DataExtractorService(DataExtractorRepository dataExtractorRepository) {
        this.dataExtractorRepository = dataExtractorRepository;
    }

    public DataExtractor createDataExtractor(DataExtractor dataExtractor) {
        return dataExtractorRepository.save(dataExtractor);
    }

    public List<DataExtractor> getAllDataExtractors() {
        return dataExtractorRepository.findAll();
    }

    public Optional<DataExtractor> getDataExtractorById(Long id) {
        return dataExtractorRepository.findById(id);
    }

    public List<DataExtractor> getDataExtractorsByHttpRequestId(Long httpRequestId) {
        return dataExtractorRepository.findByHttpRequestId(httpRequestId);
    }

    public DataExtractor updateDataExtractor(Long id, DataExtractor updatedDetails) {
        return dataExtractorRepository.findById(id).map(existing -> {
            existing.setHttpRequestId(updatedDetails.getHttpRequestId());
            existing.setName(updatedDetails.getName());
            existing.setExtractorType(updatedDetails.getExtractorType());
            existing.setVariableName(updatedDetails.getVariableName());
            existing.setExpression(updatedDetails.getExpression());
            existing.setMatchNumber(updatedDetails.getMatchNumber());
            existing.setDefaultValue(updatedDetails.getDefaultValue());
            existing.setUpdatedBy(updatedDetails.getUpdatedBy());
            return dataExtractorRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Data Extractor not found with id: " + id));
    }

    public void deleteDataExtractor(Long id) {
        dataExtractorRepository.deleteById(id);
    }
}