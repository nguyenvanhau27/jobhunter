package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private static final int PAGE_SIZE = 10;

    private final JobService jobService;
    private final SkillRepository skillRepository;

    public JobController(JobService jobService, SkillRepository skillRepository) {
        this.jobService = jobService;
        this.skillRepository = skillRepository;
    }

    // ─── UC6 + UC8: Danh sách + Filter ──────────────────────────
    @GetMapping
    public String jobList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) AppEnums.JobType jobType,
            @RequestParam(required = false) AppEnums.ExperienceLevel experienceLevel,
            @RequestParam(required = false) Long skillId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        boolean hasFilter = (keyword != null && !keyword.isBlank())
                || (location != null && !location.isBlank())
                || jobType != null
                || experienceLevel != null
                || skillId != null;

        Page<Job> jobPage = hasFilter
                ? jobService.filterJobs(keyword, location, jobType, experienceLevel, skillId, page, PAGE_SIZE)
                : jobService.findOpenJobs(page, PAGE_SIZE);

        // Filter dropdowns
        List<Skill> allSkills = skillRepository.findAll();

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("jobTypes", AppEnums.JobType.values());
        model.addAttribute("experienceLevels", AppEnums.ExperienceLevel.values());

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