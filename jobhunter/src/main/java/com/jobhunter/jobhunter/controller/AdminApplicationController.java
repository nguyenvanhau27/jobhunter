package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.service.AdminApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/jobs")
public class AdminApplicationController {

    private final AdminApplicationService adminApplicationService;

    public AdminApplicationController(AdminApplicationService adminApplicationService) {
        this.adminApplicationService = adminApplicationService;
    }

    @PostMapping("/{jobId}/applications/{appId}/approve")
    public String approve(
            @PathVariable Long jobId,
            @PathVariable Long appId,
            RedirectAttributes redirectAttributes) {

        adminApplicationService.review(appId, AppEnums.ApplicationStatus.APPROVED);
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt đơn thành công!");
        return "redirect:/admin/jobs/" + jobId + "/applications";
    }

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