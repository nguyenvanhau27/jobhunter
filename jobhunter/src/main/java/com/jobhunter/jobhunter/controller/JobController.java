package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

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
            Model model) {

        boolean hasFilter = (keyword != null && !keyword.isBlank())
                || (location != null && !location.isBlank())
                || jobType != null
                || experienceLevel != null
                || skillId != null;

        List<Job> jobs = hasFilter
                ? jobService.filterJobs(keyword, location, jobType, experienceLevel, skillId)
                : jobService.findOpenJobs();

        model.addAttribute("jobs", jobs);
        model.addAttribute("allSkills", skillRepository.findAll());
        model.addAttribute("jobTypes", AppEnums.JobType.values());
        model.addAttribute("experienceLevels", AppEnums.ExperienceLevel.values());

        // Giữ lại giá trị filter đã chọn để hiển thị lại trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("selectedJobType", jobType);
        model.addAttribute("selectedExperience", experienceLevel);
        model.addAttribute("selectedSkillId", skillId);

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