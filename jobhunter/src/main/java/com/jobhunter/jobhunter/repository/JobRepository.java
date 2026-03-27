package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // UC6: Lấy tất cả job đang OPEN
    List<Job> findByStatusJob(AppEnums.JobStatus statusJob);

    // UC8: Tìm kiếm theo keyword (title hoặc description)
    @Query("SELECT j FROM Job j WHERE j.statusJob = 'OPEN' AND " +
            "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

    // UC8: Filter theo location
    List<Job> findByStatusJobAndLocationContainingIgnoreCase(
            AppEnums.JobStatus statusJob, String location);

    // UC8: Filter theo jobType
    List<Job> findByStatusJobAndJobType(
            AppEnums.JobStatus statusJob, AppEnums.JobType jobType);

    // UC8: Filter theo experienceLevel
    List<Job> findByStatusJobAndExperienceLevel(
            AppEnums.JobStatus statusJob, AppEnums.ExperienceLevel experienceLevel);

    // UC8: Filter theo salary range
    List<Job> findByStatusJobAndMinSalaryGreaterThanEqualAndMaxSalaryLessThanEqual(
            AppEnums.JobStatus statusJob, Long minSalary, Long maxSalary);

    // UC8: Filter kết hợp nhiều tiêu chí
    @Query("SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN j.skills s " +
            "WHERE j.statusJob = 'OPEN' " +
            "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:jobType IS NULL OR j.jobType = :jobType) " +
            "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
            "AND (:skillId IS NULL OR s.id = :skillId)")
    List<Job> filterJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") AppEnums.JobType jobType,
            @Param("experienceLevel") AppEnums.ExperienceLevel experienceLevel,
            @Param("skillId") Long skillId);
}