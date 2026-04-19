package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.Application;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {

    Application apply(Long userId, Long jobId, MultipartFile cvFile, String coverLetter);

    List<Application> getByUserId(Long userId);

    List<Application> searchMyApplications(Long userId, String companyName,
                                           int page, int pageSize);

    int countMyApplications(Long userId, String companyName);
}