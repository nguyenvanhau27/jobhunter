package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.AdminJobService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/jobs")
public class AdminJobController {

    private final AdminJobService adminJobService;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;

    public AdminJobController(AdminJobService adminJobService,
                              CompanyRepository companyRepository,
                              SkillRepository skillRepository) {
        this.adminJobService = adminJobService;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
    }

    // ─── Helper: load dropdown data vào model ───────────────────
    private void loadFormData(Model model) {
        List<Company> companies = companyRepository.findAll();
        List<Skill> allSkills = skillRepository.findAll();
        model.addAttribute("companies", companies);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("jobTypes", AppEnums.JobType.values());
        model.addAttribute("experienceLevels", AppEnums.ExperienceLevel.values());
    }

    // ─── GET /admin/jobs — danh sách job ────────────────────────
    @GetMapping
    public String jobList(Model model) {
        model.addAttribute("jobs", adminJobService.findAll());
        return "admin/job/list";
    }

    // ─── GET /admin/jobs/create ──────────────────────────────────
    @GetMapping("/create")
    public String createForm(Model model) {
        List<Company> companies = companyRepository.findAll();

        // E1: Chưa có company
        if (companies.isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng tạo công ty trước khi tạo job!");
            return "admin/job/list";
        }

        loadFormData(model);
        model.addAttribute("jobDTO", new JobDTO());
        return "admin/job/form";
    }

    // ─── POST /admin/jobs/create ─────────────────────────────────
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("jobDTO") JobDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "admin/job/form";
        }

        adminJobService.createJob(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo job thành công!");
        return "redirect:/admin/jobs";
    }

    // ─── GET /admin/jobs/{id}/edit ───────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Job job = adminJobService.findById(id);

        // Map job → DTO để pre-fill form
        JobDTO dto = new JobDTO();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setCompanyId(job.getCompany().getId());
        dto.setSkillIds(job.getSkills().stream()
                .map(s -> s.getId())
                .collect(java.util.stream.Collectors.toList()));

        loadFormData(model);
        model.addAttribute("jobDTO", dto);
        model.addAttribute("jobId", id);
        return "admin/job/form";
    }

    // ─── POST /admin/jobs/{id}/edit ──────────────────────────────
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("jobDTO") JobDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadFormData(model);
            model.addAttribute("jobId", id);
            return "admin/job/form";
        }

        adminJobService.updateJob(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật job thành công!");
        return "redirect:/admin/jobs";
    }

    // ─── POST /admin/jobs/{id}/delete ────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        String result = adminJobService.deleteJob(id);
        redirectAttributes.addFlashAttribute("successMessage", result);
        return "redirect:/admin/jobs";
    }
}