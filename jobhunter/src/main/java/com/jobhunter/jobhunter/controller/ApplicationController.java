package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.service.ApplicationService;
import com.jobhunter.jobhunter.service.JobService;
import com.jobhunter.jobhunter.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final UserService userService;

    public ApplicationController(ApplicationService applicationService,
                                 JobService jobService,
                                 UserService userService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.userService = userService;
    }


    @GetMapping("/jobs/{id}/apply")
    public String applyForm(
            @PathVariable Long id,
            Model model) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        return "user/apply";
    }


    @PostMapping("/jobs/{id}/apply")
    public String apply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("cvFile") MultipartFile cvFile,
            @RequestParam("coverLetter") String coverLetter,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            applicationService.apply(user.getId(), id, cvFile, coverLetter);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Nộp đơn thành công! Chúc bạn may mắn.");
            return "redirect:/applications/my";

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/jobs/" + id;
        }
    }


    @GetMapping("/applications/my")
    public String myApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Application> applications = applicationService.getByUserId(user.getId());
        model.addAttribute("applications", applications);
        return "user/my-applications";
    }
}