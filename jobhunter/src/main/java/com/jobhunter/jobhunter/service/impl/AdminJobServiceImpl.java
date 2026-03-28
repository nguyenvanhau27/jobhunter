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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));
    }

    // ─── Create Job ─────────────────────────────────────────────
    @Override
    @Transactional
    public Job createJob(JobDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setCompany(company);
        job.setStatusJob(AppEnums.JobStatus.OPEN);

        // Gán skills
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(dto.getSkillIds()));
            job.setSkills(skills);
        }

        Job saved = jobRepository.save(job);

        System.out.println("=================================");
        System.out.println("JOB CREATED");
        System.out.println("Title   : " + saved.getTitle());
        System.out.println("Company : " + company.getNameCompany());
        System.out.println("Skills  : " + saved.getSkills().size());
        System.out.println("=================================");

        return saved;
    }

    // ─── Update Job ─────────────────────────────────────────────
    @Override
    @Transactional
    public Job updateJob(Long id, JobDTO dto) {
        Job job = findById(id);

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company không tồn tại"));

        // Cập nhật tất cả field
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setCompany(company);

        // Cập nhật skills: xoá cũ → set mới
        // JPA @ManyToMany tự xử lý delete/insert job_skills
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            Set<Skill> newSkills = new HashSet<>(skillRepository.findAllById(dto.getSkillIds()));
            job.setSkills(newSkills);
        } else {
            job.setSkills(new HashSet<>());
        }

        System.out.println("=================================");
        System.out.println("JOB UPDATED");
        System.out.println("Id      : " + id);
        System.out.println("Title   : " + job.getTitle());
        System.out.println("Skills  : " + job.getSkills().size());
        System.out.println("=================================");

        return jobRepository.save(job);
    }

    // ─── Delete Job ─────────────────────────────────────────────
    @Override
    @Transactional
    public String deleteJob(Long id) {
        Job job = findById(id);

        // A1: Có Application PENDING → CLOSED thay vì xoá
        boolean hasPending = applicationRepository
                .existsByJobIdAndStatus(id, AppEnums.ApplicationStatus.PENDING);

        if (hasPending) {
            job.setStatusJob(AppEnums.JobStatus.CLOSED);
            jobRepository.save(job);

            System.out.println("=================================");
            System.out.println("JOB CLOSED (has pending applications)");
            System.out.println("Id : " + id);
            System.out.println("=================================");

            return "Job \"" + job.getTitle() + "\" đã được chuyển sang CLOSED vì còn đơn ứng tuyển đang chờ duyệt.";
        }

        // Không có PENDING → xoá cứng
        // JPA @ManyToMany cascade tự xoá job_skills
        jobRepository.delete(job);

        System.out.println("=================================");
        System.out.println("JOB DELETED");
        System.out.println("Id : " + id);
        System.out.println("=================================");

        return "Đã xoá job \"" + job.getTitle() + "\" thành công!";
    }
}