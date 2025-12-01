package com.jobportal.jobportal_api.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.jobportal_api.entity.Job;
import com.jobportal.jobportal_api.repository.JobRepository;

@Service
public class JobService {
    private static final Logger logger = LogManager.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepo;


    public Job create(Job job) {
        logger.info("Creating job: {}", job.getTitle());
        return jobRepo.save(job);
    }

    public Optional<Job> update(Long id, Job job) {
        return jobRepo.findById(id).map(existing -> {
            existing.setTitle(job.getTitle());
            existing.setDescription(job.getDescription());
            existing.setCompanyName(job.getCompanyName());
            existing.setLocation(job.getLocation());
            existing.setSalary(job.getSalary());
            logger.info("Updating job id {}", id);
            return jobRepo.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!jobRepo.existsById(id)) {
            logger.warn("Attempt to delete non-existing job id {}", id);
            return false;
        }
        jobRepo.deleteById(id);
        logger.info("Deleted job id {}", id);
        return true;
    }

    public List<Job> listAll() {
        logger.debug("Fetching all jobs");
        return jobRepo.findAll();
    }

    public Optional<Job> findById(Long id) {
        logger.debug("Fetching job by id {}", id);
        return jobRepo.findById(id);
    }
}