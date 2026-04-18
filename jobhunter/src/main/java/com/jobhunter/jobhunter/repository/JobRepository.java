package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.dto.JobListItemDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Danh sách job đang mở cho USER/GUEST
    @Query("SELECT j FROM Job j WHERE j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now)")
    Page<Job> findActiveJobs(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
            SELECT DISTINCT j FROM Job j LEFT JOIN j.skills s
            WHERE j.statusJob = 'OPEN'
            AND (j.expiredAt IS NULL OR j.expiredAt > :now)
            AND (:keyword  IS NULL OR LOWER(j.title)    LIKE LOWER(CONCAT('%', :keyword,  '%')))
            AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:jobTypes  IS NULL OR j.jobType         IN :jobTypes)
            AND (:expLevels IS NULL OR j.experienceLevel IN :expLevels)
            AND (:skillIds  IS NULL OR s.id              IN :skillIds)
            """)
    Page<Job> filterActiveJobs(
            @Param("now")       LocalDateTime now,
            @Param("keyword")   String keyword,
            @Param("location")  String location,
            @Param("jobTypes")  List<AppEnums.JobType> jobTypes,
            @Param("expLevels") List<AppEnums.ExperienceLevel> expLevels,
            @Param("skillIds")  List<Long> skillIds,
            Pageable pageable);

    // Admin: tất cả job kể cả CLOSED/expired
    Page<Job> findAll(Pageable pageable);

    // Scheduler: job OPEN đã quá hạn
    @Query("SELECT j FROM Job j WHERE j.statusJob = 'OPEN' " +
            "AND j.expiredAt IS NOT NULL AND j.expiredAt < :now")
    List<Job> findExpiredOpenJobs(@Param("now") LocalDateTime now);

    // Dashboard stats
    long countByStatusJob(AppEnums.JobStatus statusJob);

    // Trending: top job nhiều đơn nhất trong 7 ngày gần đây
    @Query("SELECT j FROM Job j " +
            "JOIN Application a ON a.job.id = j.id " +
            "WHERE j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now) " +
            "AND a.appliedAt >= :since " +
            "GROUP BY j ORDER BY COUNT(a) DESC")
    List<Job> findTrendingJobs(@Param("now") LocalDateTime now,
                               @Param("since") LocalDateTime since,
                               Pageable pageable);

    // Admin job list với tổng ứng viên + số PENDING
    @Query("""
            SELECT new com.jobhunter.jobhunter.dto.JobListItemDTO(
                j.id, j.title, j.company.nameCompany,
                CAST(j.statusJob AS string),
                j.expiredAt, j.updatedAt,
                COUNT(a.id),
                SUM(CASE WHEN a.status = "PENDING" THEN 1L ELSE 0L END)
            )
            FROM Job j LEFT JOIN Application a ON a.job.id = j.id
            GROUP BY j.id, j.title, j.company.nameCompany,
                     j.statusJob, j.expiredAt, j.updatedAt
            ORDER BY MAX(a.appliedAt) DESC NULLS LAST, j.createdAt DESC
            """)
    Page<JobListItemDTO> findAllWithCandidateCounts(Pageable pageable);

    // Đếm số đơn theo jobId (dùng cho job detail page)
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.id = :jobId")
    long countApplicationsByJobId(@Param("jobId") Long jobId);

    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId " +
            "AND j.statusJob = 'OPEN' " +
            "AND (j.expiredAt IS NULL OR j.expiredAt > :now) " +
            "ORDER BY j.createdAt DESC")
    List<Job> findOpenJobsByCompanyId(@Param("companyId") Long companyId,
                                      @Param("now") LocalDateTime now);
}