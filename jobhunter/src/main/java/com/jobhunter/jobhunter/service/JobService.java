package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobService {

    Page<Job> findActiveJobs(int page, int size);

    Job findById(Long id);

    Page<Job> filterJobs(String keyword, String location,
                         List<AppEnums.JobType> jobTypes,
                         List<AppEnums.ExperienceLevel> experienceLevels,
                         List<Long> skillIds, int page, int size);

    Page<Job> findOpenJobs(int page, int size);

    // Trending: top job nhiều apply nhất trong 7 ngày
    List<Job> findTrendingJobs(int limit);


}