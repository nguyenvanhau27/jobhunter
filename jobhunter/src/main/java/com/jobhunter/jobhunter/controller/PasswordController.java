package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.service.PasswordService;
import com.jobhunter.jobhunter.service.PasswordService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // ══════════════════════════════════════════
    // CHANGE PASSWORD (đã đăng nhập)
    // ══════════════════════════════════════════
//
//    @GetMapping("/profile/change-password")
//    public String changePasswordPage() {
//        return "user/change-password";
//    }
//
//    @PostMapping("/profile/change-password")
//    public String changePassword(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam String oldPassword,
//            @RequestParam String newPassword,
//            @RequestParam String confirmPassword,
//            RedirectAttributes redirectAttributes) {
//
//        if (!newPassword.equals(confirmPassword)) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                    "Mật khẩu mới và xác nhận không khớp");
//            return "redirect:/profile/change-password";
//        }
//
//        try {
//            passwordService.changePassword(
//                    userDetails.getUsername(), oldPassword, newPassword);
//            redirectAttributes.addFlashAttribute("successMessage",
//                    "Đổi mật khẩu thành công!");
//            return "redirect:/profile";
//
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/profile/change-password";
//        }
//    }

    // ══════════════════════════════════════════
    // FORGOT PASSWORD (chưa đăng nhập)
    // ══════════════════════════════════════════

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {
            passwordService.sendResetEmail(email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/forgot-password";
    }

    // ══════════════════════════════════════════
    // RESET PASSWORD (từ link trong email)
    // ══════════════════════════════════════════

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mật khẩu và xác nhận không khớp");
            return "redirect:/reset-password?token=" + token;
        }

        try {
            passwordService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }
}