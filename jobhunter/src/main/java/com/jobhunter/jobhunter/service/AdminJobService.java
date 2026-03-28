package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.entity.Job;

import java.util.List;

public interface AdminJobService {

    List<Job> findAll();

    Job findById(Long id);

    Job createJob(JobDTO dto);

    Job updateJob(Long id, JobDTO dto);

    // Trả về message — CLOSED hoặc DELETED
    String deleteJob(Long id);
}