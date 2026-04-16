package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Page<Job> findActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findActiveJobs(LocalDateTime.now(), pageable);
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
    }

    @Override
    public Page<Job> filterJobs(String keyword, String location,
                                List<AppEnums.JobType> jobTypes,
                                List<AppEnums.ExperienceLevel> experienceLevels,
                                List<Long> skillIds,
                                int page, int size) {
        String kw  = (keyword  != null && !keyword.isBlank())  ? keyword  : null;
        String loc = (location != null && !location.isBlank()) ? location : null;
        List<AppEnums.JobType> jt  = (jobTypes != null && !jobTypes.isEmpty())          ? jobTypes          : null;
        List<AppEnums.ExperienceLevel> el = (experienceLevels != null && !experienceLevels.isEmpty()) ? experienceLevels : null;
        List<Long> sk = (skillIds != null && !skillIds.isEmpty())                       ? skillIds          : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.filterActiveJobs(LocalDateTime.now(), kw, loc, jt, el, sk, pageable);
    }

    @Override
    public Page<Job> findOpenJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findActiveJobs(LocalDateTime.now(), pageable);
    }

    @Override
    public List<Job> findTrendingJobs(int limit) {
        // Job have most application in 7 day
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return jobRepository.findTrendingJobs(
                LocalDateTime.now(), since,
                PageRequest.of(0, limit));
    }

}