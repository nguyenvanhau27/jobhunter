package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.service.CompanyService;
import com.jobhunter.jobhunter.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final JobService jobService;
    private final CompanyService companyService;

    public HomeController(JobService jobService, CompanyService companyService) {
        this.jobService = jobService;
        this.companyService = companyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Job> trendingJobs = jobService.findTrendingJobs(8);
        List<Company> topCompanies = companyService.findTopCompanies(8);

        model.addAttribute("trendingJobs", trendingJobs);
        model.addAttribute("topCompanies", topCompanies);
        return "home";
    }
}