package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findOpenJobs() {
        return jobRepository.findByStatusJob(AppEnums.JobStatus.OPEN);
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
    }

    @Override
    public List<Job> filterJobs(String keyword, String location,
                                AppEnums.JobType jobType,
                                AppEnums.ExperienceLevel experienceLevel,
                                Long skillId) {
        // Chuẩn hoá input — empty string → null để query JPQL xử lý đúng
        String kw = (keyword != null && !keyword.isBlank()) ? keyword : null;
        String loc = (location != null && !location.isBlank()) ? location : null;

        return jobRepository.filterJobs(kw, loc, jobType, experienceLevel, skillId);
    }
}