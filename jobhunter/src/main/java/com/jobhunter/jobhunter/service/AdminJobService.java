package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AdminJobService {

    Page<Job> findAll(int page, int size);

    Job findById(Long id);

    Job createJob(JobDTO dto);

    Job updateJob(Long id, JobDTO dto);

    Job reopenJob(Long id, LocalDateTime newExpiredAt);

    String deleteJob(Long id);
}