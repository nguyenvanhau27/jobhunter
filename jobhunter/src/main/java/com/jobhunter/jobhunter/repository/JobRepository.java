package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // UC6: Job OPEN + chưa hết hạn (USER/GUEST xem)
    @Query("SELECT j FROM Job j WHERE j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now)")
    Page<Job> findActiveJobs(@Param("now") LocalDateTime now, Pageable pageable);

    // UC8: Filter + chưa hết hạn
    @Query("SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN j.skills s " +
            "WHERE j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now) " +
            "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:jobType IS NULL OR j.jobType = :jobType) " +
            "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
            "AND (:skillId IS NULL OR s.id = :skillId)")
    Page<Job> filterActiveJobs(
            @Param("now") LocalDateTime now,
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") AppEnums.JobType jobType,
            @Param("experienceLevel") AppEnums.ExperienceLevel experienceLevel,
            @Param("skillId") Long skillId,
            Pageable pageable);

    // ADMIN: xem tất cả job (kể cả hết hạn, CLOSED)
    Page<Job> findAll(Pageable pageable);

    // Scheduled Task: tìm job OPEN đã hết hạn để tự động CLOSED
    @Query("SELECT j FROM Job j WHERE j.statusJob = 'OPEN' " +
            "AND j.expiredAt IS NOT NULL AND j.expiredAt < :now")
    List<Job> findExpiredOpenJobs(@Param("now") LocalDateTime now);

    // Scheduled Task: xoá token hết hạn (dùng trong PasswordResetTokenRepository thay thế)
    // Admin reopen: chỉ cần findById rồi update status + expiredAt
    // Dashboard stats
    long countByStatusJob(AppEnums.JobStatus statusJob);

    // Trending: top job có nhiều application nhất trong 7 ngày gần đây
    @Query("SELECT j FROM Job j " +
            "JOIN Application a ON a.job.id = j.id " +
            "WHERE j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now) " +
            "AND a.appliedAt >= :since " +
            "GROUP BY j " +
            "ORDER BY COUNT(a) DESC")
    List<Job> findTrendingJobs(@Param("now") LocalDateTime now,
                               @Param("since") LocalDateTime since,
                               Pageable pageable);

}