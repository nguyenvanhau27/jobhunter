package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.ApplicationDetailDTO;


public interface ApplicationDetailService {
    ApplicationDetailDTO getDetail(Long jobId, Long appId);
}