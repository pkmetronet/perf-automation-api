package com.performance.api.controller;

import com.performance.api.model.ThreadGroup;
import com.performance.api.service.ThreadGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/thread-groups")
public class ThreadGroupController {

    private final ThreadGroupService threadGroupService;

    public ThreadGroupController(ThreadGroupService threadGroupService) {
        this.threadGroupService = threadGroupService;
    }

    @PostMapping
    public ResponseEntity<ThreadGroup> createThreadGroup(@RequestBody ThreadGroup threadGroup) {
        ThreadGroup created = threadGroupService.createThreadGroup(threadGroup);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ThreadGroup>> getAllThreadGroups() {
        return ResponseEntity.ok(threadGroupService.getAllThreadGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreadGroup> getThreadGroupById(@PathVariable Long id) {
        return threadGroupService.getThreadGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Helpful endpoint to get all Thread Groups belonging to a specific Test Plan
    @GetMapping("/test-plan/{testPlanId}")
    public ResponseEntity<List<ThreadGroup>> getThreadGroupsByTestPlanId(@PathVariable Long testPlanId) {
        List<ThreadGroup> groups = threadGroupService.getThreadGroupsByTestPlanId(testPlanId);
        return ResponseEntity.ok(groups);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThreadGroup> updateThreadGroup(@PathVariable Long id, @RequestBody ThreadGroup threadGroup) {
        try {
            ThreadGroup updated = threadGroupService.updateThreadGroup(id, threadGroup);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThreadGroup(@PathVariable Long id) {
        threadGroupService.deleteThreadGroup(id);
        return ResponseEntity.noContent().build();
    }
}