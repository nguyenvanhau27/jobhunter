package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.service.AdminApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public AdminApplicationServiceImpl(ApplicationRepository applicationRepository,
                                       JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Application> findByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    @Override
    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại"));
    }

    @Override
    public Application review(Long applicationId, AppEnums.ApplicationStatus status) {
        Application application = findById(applicationId);
        application.setStatus(status);

        Application saved = applicationRepository.save(application);

        System.out.println("=================================");
        System.out.println("APPLICATION REVIEWED");
        System.out.println("Id     : " + applicationId);
        System.out.println("User   : " + application.getUser().getEmail());
        System.out.println("Job    : " + application.getJob().getTitle());
        System.out.println("Status : " + status);
        System.out.println("=================================");

        return saved;
    }

    @Override
    public Application findByIdEager(Long id) {
        return applicationRepository.findWithUserById(id)
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại: " + id));
    }

    @Override
    public int calcMatchingPercent(Application application, Long jobId) {
        if (application.getUser() == null
                || application.getUser().getUserSkills() == null) return 0;

        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null || job.getSkills() == null || job.getSkills().isEmpty()) return 0;

        Set<Long> jobSkillIds = job.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> userSkillIds = application.getUser().getUserSkills().stream()
                .filter(us -> us.getSkill() != null)
                .map(us -> us.getSkill().getId())
                .collect(Collectors.toSet());

        long matched = userSkillIds.stream()
                .filter(jobSkillIds::contains)
                .count();

        return (int) Math.round((double) matched / jobSkillIds.size() * 100);
    }
}