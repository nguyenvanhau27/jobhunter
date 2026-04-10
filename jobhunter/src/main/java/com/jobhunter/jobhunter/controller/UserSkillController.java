package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.service.UserService;
import com.jobhunter.jobhunter.service.UserSkillService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/skills")
public class UserSkillController {

    private static final int PAGE_SIZE = 20;

    private final UserSkillService userSkillService;
    private final UserService userService;
    private final SkillRepository skillRepository;

    public UserSkillController(UserSkillService userSkillService,
                               UserService userService,
                               SkillRepository skillRepository) {
        this.userSkillService = userSkillService;
        this.userService = userService;
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public String skillsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        User user = userService.findByEmail(userDetails.getUsername());

        List<UserSkill> allMySkills = userSkillService.getSkillsByUserId(user.getId());

        // Pagination
        int totalSkills = allMySkills.size();
        int totalPages  = Math.max(1, (int) Math.ceil((double) totalSkills / PAGE_SIZE));
        int safePage    = Math.min(page, totalPages - 1);
        int start       = safePage * PAGE_SIZE;
        int end         = Math.min(start + PAGE_SIZE, totalSkills);
        List<UserSkill> pagedSkills = totalSkills > 0 ? allMySkills.subList(start, end) : List.of();

        // All system skills for search dropdown
        List<Skill> allSkills = skillRepository.findAllByOrderByCategoryAscNameAsc();

        // Unique categories for filter dropdown
        List<String> categories = allSkills.stream()
                .map(Skill::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct().sorted()
                .collect(Collectors.toList());

        model.addAttribute("mySkills",    pagedSkills);
        model.addAttribute("allSkills",   allSkills);
        model.addAttribute("categories",  categories);
        model.addAttribute("levels",      AppEnums.SkillLevel.values());
        model.addAttribute("user",        user);
        model.addAttribute("totalSkills", totalSkills);
        model.addAttribute("totalPages",  totalPages);
        model.addAttribute("currentPage", safePage);
        return "user/skills";
    }

    @PostMapping("/add")
    public String addSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            @RequestParam AppEnums.SkillLevel level,
            RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        try {
            userSkillService.addSkill(user.getId(), skillId, level);
            ra.addFlashAttribute("successMessage", "Thêm skill thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/skills";
    }

    @PostMapping("/update")
    public String updateSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            @RequestParam AppEnums.SkillLevel level,
            RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        try {
            userSkillService.updateSkillLevel(user.getId(), skillId, level);
            ra.addFlashAttribute("successMessage", "Cập nhật level thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/skills";
    }

    @PostMapping("/remove")
    public String removeSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        try {
            userSkillService.removeSkill(user.getId(), skillId);
            ra.addFlashAttribute("successMessage", "Đã xoá skill!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/skills";
    }
}