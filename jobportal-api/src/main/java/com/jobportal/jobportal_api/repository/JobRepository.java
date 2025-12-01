package com.jobportal.jobportal_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.jobportal_api.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

}
