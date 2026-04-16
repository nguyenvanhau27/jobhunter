package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.ProfileDTO;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.service.PasswordService;
import com.jobhunter.jobhunter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final PasswordService passwordService;

    public ProfileController(UserService userService, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
    }

    private void loadUser(Model model, UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername());

        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setExperience(user.getExperience());

        model.addAttribute("user", user);
        model.addAttribute("profileDTO", dto);
    }


    @GetMapping
    public String show(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "info") String tab,
            Model model) {

        loadUser(model, ud);

        // Flash attribute (từ redirect) được ưu tiên hơn param
        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", tab);
        }
        return "user/profile";
    }


    @PostMapping("/update")
    public String update(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @ModelAttribute("profileDTO") ProfileDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            loadUser(model, ud);
            model.addAttribute("activeTab", "info");
            return "user/profile";
        }

        userService.updateProfile(ud.getUsername(), dto);
        ra.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        ra.addFlashAttribute("activeTab", "info");
        return "redirect:/profile";
    }


    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes ra) {

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("pwdError", "Mật khẩu mới và xác nhận không khớp");
            ra.addFlashAttribute("activeTab", "password");
            return "redirect:/profile?tab=password";
        }

        try {
            passwordService.changePassword(ud.getUsername(), oldPassword, newPassword);
            ra.addFlashAttribute("pwdSuccess", "Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("pwdError", e.getMessage());
        }

        ra.addFlashAttribute("activeTab", "password");
        return "redirect:/profile?tab=password";
    }
}