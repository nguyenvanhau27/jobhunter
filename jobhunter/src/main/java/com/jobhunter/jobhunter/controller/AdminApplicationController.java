package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.service.AdminApplicationService;
import com.jobhunter.jobhunter.service.AdminJobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/jobs")
public class AdminApplicationController {

    private final AdminApplicationService adminApplicationService;
    private final AdminJobService adminJobService;

    public AdminApplicationController(AdminApplicationService adminApplicationService,
                                      AdminJobService adminJobService) {
        this.adminApplicationService = adminApplicationService;
        this.adminJobService = adminJobService;
    }

    // ─── UC16: Xem danh sách ứng viên theo job ──────────────────
//    @GetMapping("/{jobId}/applications")
//    public String applicantList(@PathVariable Long jobId, Model model) {
//        Job job = adminJobService.findById(jobId);
//        List<Application> applications = adminApplicationService.findByJobId(jobId);
//
//        model.addAttribute("job", job);
//        model.addAttribute("applications", applications);
//        return "admin/application/list";
//    }

//    @GetMapping("/{jobId}/applications/{appId}")
//    public String applicantDetail(
//            @PathVariable Long jobId,
//            @PathVariable Long appId,
//            Model model) {
//
//        Job job = adminJobService.findById(jobId);
//
//        // ← EntityGraph eager load user + userSkills + skill
//        Application application = adminApplicationService.findByIdEager(appId);
//
//        // Tính matching percent — service tự xử lý null-safe
//        int matchingPct = adminApplicationService.calcMatchingPercent(application, jobId);
//
//        model.addAttribute("job", job);
//        model.addAttribute("application", application);
//        model.addAttribute("matchingPct", matchingPct);
//        model.addAttribute("now", LocalDateTime.now());
//        return "admin/application/detail";
//    }

    // ─── UC17: Duyệt đơn — APPROVED ─────────────────────────────
    @PostMapping("/{jobId}/applications/{appId}/approve")
    public String approve(
            @PathVariable Long jobId,
            @PathVariable Long appId,
            RedirectAttributes redirectAttributes) {

        adminApplicationService.review(appId, AppEnums.ApplicationStatus.APPROVED);
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt đơn thành công!");
        return "redirect:/admin/jobs/" + jobId + "/applications";
    }

    // ─── UC17: Từ chối đơn — REJECTED ───────────────────────────
    @PostMapping("/{jobId}/applications/{appId}/reject")
    public String reject(
            @PathVariable Long jobId,
            @PathVariable Long appId,
            RedirectAttributes redirectAttributes) {

        adminApplicationService.review(appId, AppEnums.ApplicationStatus.REJECTED);
        redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn!");
        return "redirect:/admin/jobs/" + jobId + "/applications";
    }
}