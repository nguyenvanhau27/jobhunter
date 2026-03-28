package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobService {


    // UC6: Danh sách job OPEN có phân trang
    Page<Job> findOpenJobs(int page, int size);

    // UC7: Chi tiết job
    Job findById(Long id);

    // UC8: Filter + phân trang
    Page<Job> filterJobs(String keyword, String location,
                         AppEnums.JobType jobType,
                         AppEnums.ExperienceLevel experienceLevel,
                         Long skillId,
                         int page, int size);
}