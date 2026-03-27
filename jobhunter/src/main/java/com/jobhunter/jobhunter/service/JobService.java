package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;

import java.util.List;

public interface JobService {

    // UC6: Xem danh sách job
    List<Job> findOpenJobs();

    // UC7: Xem chi tiết job
    Job findById(Long id);

    // UC8: Filter job kết hợp nhiều tiêu chí
    List<Job> filterJobs(String keyword, String location,
                         AppEnums.JobType jobType,
                         AppEnums.ExperienceLevel experienceLevel,
                         Long skillId);
}