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

@Controller
@RequestMapping("/user/skills")
public class UserSkillController {

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

    // ─── GET /profile/skills ─────────────────────────────────────
    @GetMapping
    public String skillsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User user = userService.findByEmail(userDetails.getUsername());

        // Danh sách skill hiện tại của user
        List<UserSkill> mySkills = userSkillService.getSkillsByUserId(user.getId());

        // Tất cả skill trong hệ thống để hiển thị dropdown
        List<Skill> allSkills = skillRepository.findAll();

        // Tất cả level để hiển thị dropdown
        AppEnums.SkillLevel[] levels = AppEnums.SkillLevel.values();

        model.addAttribute("mySkills", mySkills);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("levels", levels);
        model.addAttribute("user", user);

        return "user/skills";
    }

    // ─── POST /profile/skills/add ────────────────────────────────
    @PostMapping("/add")
    public String addSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            @RequestParam AppEnums.SkillLevel level,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            userSkillService.addSkill(user.getId(), skillId, level);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm skill thành công!");

        } catch (IllegalArgumentException e) {
            // E1: Skill đã tồn tại
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/skills";
    }

    // ─── POST /profile/skills/update ────────────────────────────
    @PostMapping("/update")
    public String updateSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            @RequestParam AppEnums.SkillLevel level,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            userSkillService.updateSkillLevel(user.getId(), skillId, level);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật level thành công!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/skills";
    }

    // ─── POST /profile/skills/remove ────────────────────────────
    @PostMapping("/remove")
    public String removeSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long skillId,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            userSkillService.removeSkill(user.getId(), skillId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xoá skill!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/skills";
    }
}