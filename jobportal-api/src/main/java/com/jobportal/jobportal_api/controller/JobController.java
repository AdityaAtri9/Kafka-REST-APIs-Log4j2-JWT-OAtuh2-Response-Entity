package com.jobportal.jobportal_api.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.jobportal_api.entity.Job;
import com.jobportal.jobportal_api.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private static final Logger logger = LogManager.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> all() {
        logger.info("GET /api/jobs");
        return ResponseEntity.ok(jobService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        logger.info("GET /api/jobs/{}", id);
        return jobService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Job job) {
        logger.info("POST /api/jobs - create job");
        if (job.getTitle() == null || job.getDescription() == null || job.getCompanyName() == null) {
            logger.warn("Job create validation failed");
            return ResponseEntity.badRequest().body("title, description, companyName required");
        }
        return ResponseEntity.ok(jobService.create(job));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Job job) {
        logger.info("PUT /api/jobs/{} - update", id);
        return jobService.update(id, job).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/jobs/{} - delete", id);
        boolean deleted = jobService.delete(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> apply(@PathVariable Long id, @RequestBody(required=false) String resumeText) {
        logger.info("User applying to job {}", id);
        if (!jobService.findById(id).isPresent()) return ResponseEntity.notFound().build();
        // simple action: in real app save application record
        logger.info("Application recorded (logged) for job {}", id);
        return ResponseEntity.ok(Map.of("message", "applied", "jobId", id));
    }
}