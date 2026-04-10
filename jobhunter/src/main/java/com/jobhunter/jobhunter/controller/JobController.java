package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.CompanyService;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private static final int PAGE_SIZE = 10;

    private final JobService jobService;
    private final SkillRepository skillRepository;
    private final CompanyService companyService;

    public JobController(JobService jobService, SkillRepository skillRepository, CompanyService companyService) {
        this.jobService = jobService;
        this.skillRepository = skillRepository;
        this.companyService = companyService;
    }

    // ─── UC6 + UC8: Danh sách + Filter ──────────────────────────
    @GetMapping
    public String jobList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<AppEnums.JobType> jobType,           // ← List
            @RequestParam(required = false) List<AppEnums.ExperienceLevel> experienceLevel, // ← List
            @RequestParam(required = false) List<Long> skillId,                        // ← List
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        boolean hasFilter = (keyword != null && !keyword.isBlank())
                || (location != null && !location.isBlank())
                || (jobType != null && !jobType.isEmpty())
                || (experienceLevel != null && !experienceLevel.isEmpty())
                || (skillId != null && !skillId.isEmpty());

        Page<Job> jobPage = hasFilter
                ? jobService.filterJobs(keyword, location, jobType, experienceLevel, skillId, page, PAGE_SIZE)
                : jobService.findOpenJobs(page, PAGE_SIZE);

        List<Skill> allSkills = skillRepository.findAllByOrderByCategoryAscNameAsc();
        Map<String, List<Skill>> skillsByCategory = allSkills.stream()
                .collect(Collectors.groupingBy(Skill::getCategory,
                        java.util.LinkedHashMap::new, Collectors.toList()));
        model.addAttribute("skillsByCategory", skillsByCategory);
        model.addAttribute("allSkills", allSkills); // keep as fallback
        model.addAttribute("jobTypes", AppEnums.JobType.values());
        model.addAttribute("experienceLevels", AppEnums.ExperienceLevel.values());

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("selectedJobTypes", jobType);
        model.addAttribute("selectedExperiences", experienceLevel);
        model.addAttribute("selectedSkillIds", skillId);

        // Giữ lại filter đã chọn
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("selectedJobType", jobType);
        model.addAttribute("selectedExperience", experienceLevel);
        model.addAttribute("selectedSkillId", skillId);
        model.addAttribute("currentPage", page);

        return "job/list";
    }

    // ─── UC7: Chi tiết job ───────────────────────────────────────
    @GetMapping("/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        return "job/detail";
    }
}