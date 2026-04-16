package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import java.util.List;

public interface AdminApplicationService {
    List<Application> findByJobId(Long jobId);

    Application findById(Long id);

    Application findByIdEager(Long id);   // ← dùng cho detail page

    Application review(Long applicationId, AppEnums.ApplicationStatus status);

    // Caculator % matching between user skills and job skills
    int calcMatchingPercent(Application application, Long jobId);
}
