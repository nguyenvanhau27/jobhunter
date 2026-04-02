package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AdminDashboardController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

    public AdminDashboardController(JobRepository jobRepository,
                                    UserRepository userRepository,
                                    CompanyRepository companyRepository,
                                    ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        // ─── Job stats ───────────────────────────────────────────
        model.addAttribute("totalJobs",
                jobRepository.count());
        model.addAttribute("openJobs",
                jobRepository.countByStatusJob(AppEnums.JobStatus.OPEN));
        model.addAttribute("closedJobs",
                jobRepository.countByStatusJob(AppEnums.JobStatus.CLOSED));

        // ─── Application stats ───────────────────────────────────
        model.addAttribute("totalApplications",
                applicationRepository.count());
        model.addAttribute("pendingApplications",
                applicationRepository.countByStatus(AppEnums.ApplicationStatus.PENDING));
        model.addAttribute("approvedApplications",
                applicationRepository.countByStatus(AppEnums.ApplicationStatus.APPROVED));
        model.addAttribute("rejectedApplications",
                applicationRepository.countByStatus(AppEnums.ApplicationStatus.REJECTED));

        // ─── User stats ──────────────────────────────────────────
        model.addAttribute("totalUsers",
                userRepository.count());
        model.addAttribute("activeUsers",
                userRepository.countByStatusUser(AppEnums.UserStatus.ACTIVE));

        // ─── Company stats ───────────────────────────────────────
        model.addAttribute("totalCompanies",
                companyRepository.count());

        // ─── 10 đơn ứng tuyển mới nhất ──────────────────────────
        List<Application> recentApplications = applicationRepository.findAll(
                PageRequest.of(0, 10, Sort.by("appliedAt").descending())
        ).getContent();
        model.addAttribute("recentApplications", recentApplications);

        model.addAttribute("now", LocalDateTime.now());
        return "admin/dashboard";
    }
}