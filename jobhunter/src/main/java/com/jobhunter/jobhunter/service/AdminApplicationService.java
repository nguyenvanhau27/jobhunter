package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;

import java.util.List;
public interface AdminApplicationService {
    List<Application> findByJobId(Long jobId);
    Application findById(Long id);
    Application findByIdEager(Long id);   // ← dùng cho detail page
    Application review(Long applicationId, AppEnums.ApplicationStatus status);

    // Tính % matching giữa user skills và job skills
    int calcMatchingPercent(Application application, Long jobId);
}

//public interface AdminApplicationService {
//
//    // UC16: Xem danh sách ứng viên theo job
//    List<Application> findByJobId(Long jobId);
//
//    // UC17: Duyệt / từ chối đơn
//    Application review(Long applicationId, AppEnums.ApplicationStatus status);
//
//    // Tìm đơn theo id
//    Application findById(Long id);
//
//    Application findByIdEager(Long id);
//}