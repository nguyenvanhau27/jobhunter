package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUser_IdAndJob_Id(Long userId, Long jobId);

    List<Application> findByUser_Id(Long userId);

    List<Application> findByJobId(Long jobId);

    boolean existsByJobIdAndStatus(Long jobId, AppEnums.ApplicationStatus status);

    long countByStatus(AppEnums.ApplicationStatus status);

    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);

    // ── Eager load user + userSkills + skill cho detail page ──
    // EntityGraph tránh LazyInitializationException và N+1
    @EntityGraph(attributePaths = {
            "user",
            "user.userSkills",
            "user.userSkills.skill"
    })
    Optional<Application> findWithUserById(Long id);

    // ── Eager load list theo jobId ──
    @EntityGraph(attributePaths = {
            "user",
            "user.userSkills",
            "user.userSkills.skill"
    })
    List<Application> findWithUserByJobId(Long jobId);

}
//    // ── Fetch single Application với user EAGER dùng EntityGraph ──
//    // @EntityGraph tự xử lý LAZY → EAGER cho đúng path
//    @EntityGraph(attributePaths = {"user", "user.userSkills", "user.userSkills.skill"})
//    Optional<Application> findWithUserById(Long id);
//
//    // ── Fetch list Application theo jobId với user EAGER ──────────
//    @EntityGraph(attributePaths = {"user", "user.userSkills", "user.userSkills.skill"})
//    List<Application> findWithUserByJobId(Long jobId);

//@Repository
//public interface ApplicationRepository extends JpaRepository<Application, Long> {
//
//    // Kiểm tra user đã apply job này chưa
//    boolean existsByUserIdAndJobId(Long userId, Long jobId);
//
//    // Danh sách application của user
//    List<Application> findByUserId(Long userId);
//
//    // Danh sách application theo job (Admin xem)
//    List<Application> findByJobId(Long jobId);
//
//    // Kiểm tra job có PENDING application không (dùng khi delete job)
//    boolean existsByJobIdAndStatus(Long jobId, AppEnums.ApplicationStatus status);
//    long countByStatus(AppEnums.ApplicationStatus status);
//
//
//    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
//
//    // ── THÊM MỚI: trả về List (không phân trang) với JOIN FETCH user ──
//    // Dùng cho skill matching — cần đọc user.userSkills trong service
//    @Query("SELECT a FROM Application a " +
//            "JOIN FETCH a.user u " +
//            "LEFT JOIN FETCH u.userSkills us " +
//            "LEFT JOIN FETCH us.skill " +
//            "WHERE a.job.id = :jobId " +
//            "ORDER BY a.appliedAt DESC")
//    List<Application> findByJobIdWithUserEager(@Param("jobId") Long jobId);
//
//    // ── THÊM MỚI: lấy single application với user + userSkills EAGER ──
//    // Dùng cho detail page
//    @Query("SELECT a FROM Application a " +
//            "JOIN FETCH a.user u " +
//            "LEFT JOIN FETCH u.userSkills us " +
//            "LEFT JOIN FETCH us.skill " +
//            "WHERE a.id = :id")
//    Optional<Application> findByIdWithUserEager(@Param("id") Long id);
//}
