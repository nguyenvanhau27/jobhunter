package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.ProfileDTO;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // ─── GET /profile — hiển thị form với dữ liệu hiện tại ──────
    @GetMapping
    public String profilePage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        // Lấy user từ DB theo email đang đăng nhập
        User user = userService.findByEmail(userDetails.getUsername());

        // Map dữ liệu hiện tại vào DTO để pre-fill form
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setExperience(user.getExperience());

        model.addAttribute("profileDTO", dto);
        model.addAttribute("user", user);  // để hiển thị email (read-only)

        return "user/profile";
    }

    // ─── POST /profile — lưu cập nhật ───────────────────────────
    @PostMapping
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("profileDTO") ProfileDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Lỗi validation
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("user", user);
            return "user/profile";
        }

        userService.updateProfile(userDetails.getUsername(), dto);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");

        return "redirect:/profile";
    }
}