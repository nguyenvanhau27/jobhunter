package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.Application;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {

    Application apply(Long userId, Long jobId, MultipartFile cvFile, String coverLetter);

    List<Application> getByUserId(Long userId);
}