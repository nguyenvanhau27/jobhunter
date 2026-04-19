package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.service.CompanyService;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/companies")
public class CompanyController {

    private static final int PAGE_SIZE = 6;

    private final CompanyService companyService;
    private final JobService jobService;

    public CompanyController(CompanyService companyService, JobService jobService) {
        this.companyService = companyService;
        this.jobService = jobService;
    }


    @GetMapping
    public String list(
            @RequestParam(required = false)   String keyword,
            @RequestParam(defaultValue = "0") int    page,
            Model model) {

        Page<Company> companyPage = companyService.searchCompanies(keyword, page, PAGE_SIZE);

        java.util.Map<Long, Long> openJobCounts = new java.util.LinkedHashMap<>();
        for (Company c : companyPage.getContent()) {
            openJobCounts.put(c.getId(), companyService.countOpenJobs(c.getId()));
        }

        boolean isFiltering = keyword != null && !keyword.isBlank();

        model.addAttribute("companies",    companyPage.getContent());
        model.addAttribute("companyPage",  companyPage);
        model.addAttribute("openJobCounts", openJobCounts);
        model.addAttribute("keyword",      keyword);
        model.addAttribute("currentPage",  page);
        model.addAttribute("totalPages",   companyPage.getTotalPages());
        model.addAttribute("totalCompanies", companyPage.getTotalElements());
        model.addAttribute("isFiltering",  isFiltering);

        return "company/list";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Company company = companyService.findById(id);
        long openJobCount = companyService.countOpenJobs(id);

        List<Job> openJobs = jobService.findOpenJobsByCompanyId(id);

        model.addAttribute("company",      company);
        model.addAttribute("openJobCount", openJobCount);
        model.addAttribute("openJobs",     openJobs);

        return "company/detail";
    }
}