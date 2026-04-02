package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobService {

    Page<Job> findActiveJobs(int page, int size);

    Job findById(Long id);

    Page<Job> filterJobs(String keyword, String location,
                         AppEnums.JobType jobType,
                         AppEnums.ExperienceLevel experienceLevel,
                         Long skillId,
                         int page, int size);

    Page<Job> findOpenJobs(int page, int size);

    // Trending: top job nhiều apply nhất trong 7 ngày
    List<Job> findTrendingJobs(int limit);


}