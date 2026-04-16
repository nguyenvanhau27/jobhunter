package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.ApplicationDetailDTO;
import com.jobhunter.jobhunter.dto.ApplicationListItemDTO;
import com.jobhunter.jobhunter.dto.JobDTO;
import com.jobhunter.jobhunter.dto.JobListItemDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.AdminJobService;
import com.jobhunter.jobhunter.service.ApplicationDetailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/admin/jobs")
public class AdminJobController {

    private static final int PAGE_SIZE = 20;

    private final AdminJobService adminJobService;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final ApplicationDetailService applicationDetailService;

    public AdminJobController(AdminJobService adminJobService,
                              CompanyRepository companyRepository,
                              SkillRepository skillRepository,
                              ApplicationDetailService applicationDetailService) {
        this.adminJobService = adminJobService;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.applicationDetailService = applicationDetailService;
    }

    private void loadFormData(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("allSkills", skillRepository.findAll());
        model.addAttribute("jobTypes", AppEnums.JobType.values());
        model.addAttribute("experienceLevels", AppEnums.ExperienceLevel.values());
        model.addAttribute("now", LocalDateTime.now());
    }

    @GetMapping
    public String jobList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<JobListItemDTO> jobPage = adminJobService.findAllWithCandidateCounts(page, PAGE_SIZE);
        model.addAttribute("jobPage", jobPage);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("now", LocalDateTime.now());
        return "admin/job/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        if (companyRepository.findAll().isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng tạo công ty trước!");
            return "admin/job/list";
        }
        loadFormData(model);
        model.addAttribute("jobDTO", new JobDTO());
        return "admin/job/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("jobDTO") JobDTO dto,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!dto.isSalaryValid()) {
            bindingResult.rejectValue("minSalary", "salary.invalid",
                    "Lương tối thiểu không được lớn hơn lương tối đa");
        }
        if (bindingResult.hasErrors()) { loadFormData(model); return "admin/job/form"; }
        try {
            adminJobService.createJob(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo job thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            loadFormData(model); return "admin/job/form";
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Job job = adminJobService.findById(id);
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
        dto.setExpiredAt(job.getExpiredAt());
        dto.setSkillIds(job.getSkills().stream()
                .map(Skill::getId).collect(java.util.stream.Collectors.toList()));
        loadFormData(model);
        model.addAttribute("jobDTO", dto);
        model.addAttribute("jobId", id);
        return "admin/job/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("jobDTO") JobDTO dto,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!dto.isSalaryValid()) {
            bindingResult.rejectValue("minSalary", "salary.invalid",
                    "Lương tối thiểu không được lớn hơn lương tối đa");
        }
        if (bindingResult.hasErrors()) {
            loadFormData(model); model.addAttribute("jobId", id); return "admin/job/form";
        }
        try {
            adminJobService.updateJob(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật job thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            loadFormData(model); model.addAttribute("jobId", id); return "admin/job/form";
        }
        return "redirect:/admin/jobs";
    }

    @PostMapping("/{id}/reopen")
    public String reopen(@PathVariable Long id, @RequestParam String newExpiredAt,
                         RedirectAttributes redirectAttributes) {
        try {
            adminJobService.reopenJob(id, LocalDateTime.parse(newExpiredAt));
            redirectAttributes.addFlashAttribute("successMessage", "Job đã được mở lại!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", adminJobService.deleteJob(id));
        return "redirect:/admin/jobs";
    }

    @GetMapping("/{jobId}/applications")
    public String applicationList(@PathVariable Long jobId,
                                  @RequestParam(defaultValue = "false") boolean sortByMatching,
                                  Model model) {
        model.addAttribute("job", adminJobService.findById(jobId));
        model.addAttribute("applications",
                adminJobService.getApplicationsWithMatching(jobId, sortByMatching));
        model.addAttribute("sortByMatching", sortByMatching);
        model.addAttribute("now", LocalDateTime.now());
        return "admin/application/list";
    }

    // FIX #3: 60 dòng → 5 dòng
    @GetMapping("/{jobId}/applications/{appId}")
    public String applicationDetail(@PathVariable Long jobId, @PathVariable Long appId,
                                    Model model) {
        model.addAttribute("job", adminJobService.findById(jobId));
        model.addAttribute("detail", applicationDetailService.getDetail(jobId, appId));
        model.addAttribute("now", LocalDateTime.now());
        return "admin/application/detail";
    }
}