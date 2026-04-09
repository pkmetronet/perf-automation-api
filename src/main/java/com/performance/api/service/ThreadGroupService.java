package com.performance.api.service;

import com.performance.api.model.ThreadGroup;
import com.performance.api.repository.ThreadGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThreadGroupService {

    private final ThreadGroupRepository threadGroupRepository;

    public ThreadGroupService(ThreadGroupRepository threadGroupRepository) {
        this.threadGroupRepository = threadGroupRepository;
    }

    public ThreadGroup createThreadGroup(ThreadGroup threadGroup) {
        return threadGroupRepository.save(threadGroup);
    }

    public List<ThreadGroup> getAllThreadGroups() {
        return threadGroupRepository.findAll();
    }

    public Optional<ThreadGroup> getThreadGroupById(Long id) {
        return threadGroupRepository.findById(id);
    }

    public List<ThreadGroup> getThreadGroupsByTestPlanId(Long testPlanId) {
        return threadGroupRepository.findByTestPlanId(testPlanId);
    }

    public ThreadGroup updateThreadGroup(Long id, ThreadGroup updatedDetails) {
        return threadGroupRepository.findById(id).map(existing -> {
            existing.setTestPlanId(updatedDetails.getTestPlanId());
            existing.setName(updatedDetails.getName());
            existing.setDescription(updatedDetails.getDescription());
            existing.setNumThreads(updatedDetails.getNumThreads());
            existing.setRampUpTime(updatedDetails.getRampUpTime());
            existing.setLoopCount(updatedDetails.getLoopCount());
            existing.setSchedulerEnabled(updatedDetails.getSchedulerEnabled());
            existing.setDuration(updatedDetails.getDuration());
            existing.setStartupDelay(updatedDetails.getStartupDelay());
            existing.setOnSampleError(updatedDetails.getOnSampleError());
            existing.setUpdatedBy(updatedDetails.getUpdatedBy());
            return threadGroupRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Thread Group not found with id: " + id));
    }

    public void deleteThreadGroup(Long id) {
        threadGroupRepository.deleteById(id);
    }
}