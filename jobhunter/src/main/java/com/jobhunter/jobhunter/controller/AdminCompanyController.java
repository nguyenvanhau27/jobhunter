package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.CompanyDTO;
import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/companies")
public class AdminCompanyController {

    private final CompanyService companyService;

    public AdminCompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "admin/company/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("companyDTO", new CompanyDTO());
        return "admin/company/form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("companyDTO") CompanyDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/company/form";
        }
        try {
            companyService.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo công ty thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/company/form";
        }
        return "redirect:/admin/companies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Company company = companyService.findById(id);

        CompanyDTO dto = new CompanyDTO();
        dto.setNameCompany(company.getNameCompany());
        dto.setDescription(company.getDescription());
        dto.setSize(company.getSize());
        dto.setLocation(company.getLocation());
        dto.setWebsite(company.getWebsite());
        dto.setImageUrl(company.getImageUrl());

        model.addAttribute("companyDTO", dto);
        model.addAttribute("companyId", id);
        return "admin/company/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("companyDTO") CompanyDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("companyId", id);
            return "admin/company/form";
        }
        try {
            companyService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật công ty thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("companyId", id);
            return "admin/company/form";
        }
        return "redirect:/admin/companies";
    }
}