package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Kiểm tra user đã apply job này chưa
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Danh sách application của user
    List<Application> findByUserId(Long userId);

    // Danh sách application theo job (Admin xem)
    List<Application> findByJobId(Long jobId);

    // Kiểm tra job có PENDING application không (dùng khi delete job)
    boolean existsByJobIdAndStatus(Long jobId, AppEnums.ApplicationStatus status);
}