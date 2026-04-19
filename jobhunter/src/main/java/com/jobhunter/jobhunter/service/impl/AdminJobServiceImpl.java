package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.ApplicationListItemDTO;
import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.dto.JobListItemDTO;
import com.jobhunter.jobhunter.entity.*;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.AdminJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AdminJobServiceImpl implements AdminJobService {

    private static final Logger log = LoggerFactory.getLogger(AdminJobServiceImpl.class);

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
        return jobRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại: " + id));
    }

    @Override
    public Page<JobListItemDTO> findAllWithCandidateCounts(int page, int size) {
        return jobRepository.findAllWithCandidateCounts(PageRequest.of(page, size));
    }

    private void validate(JobDTO dto) {
        if (dto.getMinSalary() != null && dto.getMaxSalary() != null
                && dto.getMinSalary() > dto.getMaxSalary()) {
            throw new IllegalArgumentException("Lương tối thiểu không được lớn hơn lương tối đa");
        }
        if (dto.getExpiredAt() != null
                && dto.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày hết hạn phải sau thời điểm hiện tại");
        }
    }

    @Override
    @Transactional
    public Job createJob(JobDTO dto) {
        validate(dto);

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        Job job = new Job();
        setJobFields(job, dto, company);
        job.setStatusJob(AppEnums.JobStatus.OPEN);

        Job saved = jobRepository.save(job);
        log.info("JOB CREATED | id={} | title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    @Override
    @Transactional
    public Job updateJob(Long id, JobDTO dto) {
        validate(dto);

        Job job = findById(id);
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        setJobFields(job, dto, company);

        if (job.getStatusJob() == AppEnums.JobStatus.CLOSED
                && dto.getExpiredAt() != null
                && dto.getExpiredAt().isAfter(LocalDateTime.now())) {
            job.setStatusJob(AppEnums.JobStatus.OPEN);
            log.info("JOB AUTO-REOPENED | id={}", id);
        }

        Job saved = jobRepository.save(job);
        log.info("JOB UPDATED | id={} | title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    @Override
    @Transactional
    public Job reopenJob(Long id, LocalDateTime newExpiredAt) {
        if (newExpiredAt == null || newExpiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày hết hạn mới phải sau thời điểm hiện tại");
        }
        Job job = findById(id);
        job.setExpiredAt(newExpiredAt);
        job.setStatusJob(AppEnums.JobStatus.OPEN);
        Job saved = jobRepository.save(job);
        log.info("JOB REOPENED | id={} | newExpiry={}", id, newExpiredAt);
        return saved;
    }

    @Override
    @Transactional
    public String deleteJob(Long id) {
        Job job = findById(id);
        boolean hasPending = applicationRepository
                .existsByJobIdAndStatus(id, AppEnums.ApplicationStatus.PENDING);
        if (hasPending) {
            job.setStatusJob(AppEnums.JobStatus.CLOSED);
            jobRepository.save(job);
            log.warn("JOB CLOSED (has pending) | id={}", id);
            return "Job \"" + job.getTitle() + "\" chuyển sang CLOSED vì còn đơn đang chờ duyệt.";
        }
        jobRepository.delete(job);
        log.info("JOB DELETED | id={} | title={}", id, job.getTitle());
        return "Đã xoá job \"" + job.getTitle() + "\" thành công!";
    }

    @Override
    public List<ApplicationListItemDTO> getApplicationsWithMatching(Long jobId, boolean sortByMatching) {
        Job job = findById(jobId);
        Set<Long> jobSkillIds = job.getSkills().stream()
                .map(Skill::getId).collect(Collectors.toSet());

        List<Application> applications = applicationRepository.findWithUserByJobId(jobId);

        List<ApplicationListItemDTO> dtos = applications.stream().map(app -> {
            int pct = 0;
            if (!jobSkillIds.isEmpty() && app.getUser() != null
                    && app.getUser().getUserSkills() != null) {
                Set<Long> userSkillIds = app.getUser().getUserSkills().stream()
                        .filter(us -> us.getSkill() != null)
                        .map(us -> us.getSkill().getId()).collect(Collectors.toSet());
                long common = userSkillIds.stream().filter(jobSkillIds::contains).count();
                pct = (int) Math.round((double) common / jobSkillIds.size() * 100);
            }
            return new ApplicationListItemDTO(app, pct);
        }).collect(Collectors.toList());

        if (sortByMatching) {
            dtos.sort(java.util.Comparator
                    .comparingInt(ApplicationListItemDTO::getMatchingPercent).reversed()
                    .thenComparing(ApplicationListItemDTO::getAppliedAt,
                            java.util.Comparator.reverseOrder()));
        }
        return dtos;
    }

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
            job.setSkills(new HashSet<>(skillRepository.findAllById(dto.getSkillIds())));
        } else {
            job.setSkills(new HashSet<>());
        }
    }
}