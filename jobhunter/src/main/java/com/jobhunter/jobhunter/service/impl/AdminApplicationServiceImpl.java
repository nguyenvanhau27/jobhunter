package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.service.AdminApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final ApplicationRepository applicationRepository;

    public AdminApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
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
}