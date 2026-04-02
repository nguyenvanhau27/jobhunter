package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.AdminJobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminJobServiceImpl implements AdminJobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final ApplicationRepository applicationRepository;

    public AdminJobServiceImpl(JobRepository jobRepository,
                               CompanyRepository companyRepository,
                               SkillRepository skillRepository,
                               ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Page<Job> findAll(int page, int size) {
        return jobRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
    }
    // ─── Validate chung ──────────────────────────────────────────
    private void validate(JobDTO dto) {
        // Validate salary
        if (dto.getMinSalary() != null && dto.getMaxSalary() != null
                && dto.getMinSalary() > dto.getMaxSalary()) {
            throw new IllegalArgumentException(
                    "Lương tối thiểu không được lớn hơn lương tối đa");
        }
    }

    // ─── Validate expiredAt ──────────────────────────────────────
    private void validateExpiredAt(LocalDateTime expiredAt) {
        if (expiredAt == null) return;
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Ngày hết hạn phải sau thời điểm hiện tại");
        }
    }

    // ─── Create ──────────────────────────────────────────────────
    @Override
    @Transactional
    public Job createJob(JobDTO dto) {
        validateExpiredAt(dto.getExpiredAt());

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        Job job = new Job();
        setJobFields(job, dto, company);
        job.setStatusJob(AppEnums.JobStatus.OPEN);

        return jobRepository.save(job);
    }

    // ─── Update ──────────────────────────────────────────────────
    @Override
    @Transactional
    public Job updateJob(Long id, JobDTO dto) {
        validateExpiredAt(dto.getExpiredAt());

        Job job = findById(id);
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        setJobFields(job, dto, company);

        // Nếu job đang CLOSED mà admin cập nhật expiredAt mới → tự động OPEN lại
        if (job.getStatusJob() == AppEnums.JobStatus.CLOSED
                && dto.getExpiredAt() != null
                && dto.getExpiredAt().isAfter(LocalDateTime.now())) {
            job.setStatusJob(AppEnums.JobStatus.OPEN);
            System.out.println("JOB REOPENED via update: " + id);
        }

        return jobRepository.save(job);
    }

    // ─── Reopen job (Admin gia hạn thêm thời gian) ──────────────
    @Override
    @Transactional
    public Job reopenJob(Long id, LocalDateTime newExpiredAt) {
        if (newExpiredAt == null || newExpiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Ngày hết hạn mới phải sau thời điểm hiện tại");
        }

        Job job = findById(id);
        job.setExpiredAt(newExpiredAt);
        job.setStatusJob(AppEnums.JobStatus.OPEN);

        System.out.println("=================================");
        System.out.println("JOB REOPENED");
        System.out.println("Id         : " + id);
        System.out.println("New expiry : " + newExpiredAt);
        System.out.println("=================================");

        return jobRepository.save(job);
    }

    // ─── Delete ──────────────────────────────────────────────────
    @Override
    @Transactional
    public String deleteJob(Long id) {
        Job job = findById(id);

        boolean hasPending = applicationRepository
                .existsByJobIdAndStatus(id, AppEnums.ApplicationStatus.PENDING);

        if (hasPending) {
            job.setStatusJob(AppEnums.JobStatus.CLOSED);
            jobRepository.save(job);
            return "Job \"" + job.getTitle() + "\" chuyển sang CLOSED vì còn đơn đang chờ duyệt.";
        }

        jobRepository.delete(job);
        return "Đã xoá job \"" + job.getTitle() + "\" thành công!";
    }

    // ─── Helper: set các field chung cho create/update ───────────
    private void setJobFields(Job job, JobDTO dto, Company company) {
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setExpiredAt(dto.getExpiredAt());
        job.setCompany(company);

        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(dto.getSkillIds()));
            job.setSkills(skills);
        } else {
            job.setSkills(new HashSet<>());
        }
    }
}