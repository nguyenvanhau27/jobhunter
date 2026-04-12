package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.dto.JobListItemDTO;
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
    @Query("""
        SELECT DISTINCT j FROM Job j LEFT JOIN j.skills s
        WHERE j.statusJob = 'OPEN'
        AND (j.expiredAt IS NULL OR j.expiredAt > :now)
        AND (
            (:keyword IS NOT NULL AND LOWER(j.title) LIKE LOWER(CONCAT('%',:keyword,'%')))
            OR (:location IS NOT NULL AND LOWER(j.location) LIKE LOWER(CONCAT('%',:location,'%')))
            OR (:jobTypes IS NOT NULL AND j.jobType IN :jobTypes)
            OR (:expLevels IS NOT NULL AND j.experienceLevel IN :expLevels)
            OR (:skillIds IS NOT NULL AND s.id IN :skillIds)
    
            OR (
                :keyword IS NULL 
                AND :location IS NULL
                AND :jobTypes IS NULL
                AND :expLevels IS NULL
                AND :skillIds IS NULL
            )
        )
    """)
    Page<Job> filterActiveJobs(
            @Param("now") LocalDateTime now,
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobTypes") List<AppEnums.JobType> jobTypes,
            @Param("expLevels") List<AppEnums.ExperienceLevel> expLevels,
            @Param("skillIds") List<Long> skillIds,
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

    /**
     * Lấy job list với candidateCount + pendingCount.
     * Sort: job có đơn mới nhất lên đầu (MAX appliedAt DESC NULLS LAST).
     */
    @Query("""
        SELECT new com.jobhunter.jobhunter.dto.JobListItemDTO(
            j.id,
            j.title,
            j.company.nameCompany,
            CAST(j.statusJob AS string),
            j.expiredAt,
            j.updatedAt,
            COUNT(a.id),
            SUM(CASE WHEN a.status = "PENDING"
                     THEN 1L ELSE 0L END)
        )
        FROM Job j
        LEFT JOIN Application a ON a.job.id = j.id
        GROUP BY j.id, j.title, j.company.nameCompany,
                 j.statusJob, j.expiredAt, j.updatedAt
        ORDER BY MAX(a.appliedAt) DESC NULLS LAST, j.createdAt DESC
        """)
    Page<JobListItemDTO> findAllWithCandidateCounts(Pageable pageable);

    // Đếm số ứng viên của 1 job cụ thể (dùng cho detail nếu cần)
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.id = :jobId")
    long countApplicationsByJobId(@Param("jobId") Long jobId);
}