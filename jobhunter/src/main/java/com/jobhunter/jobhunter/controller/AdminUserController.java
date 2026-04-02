package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final int PAGE_SIZE = 20;
    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ─── GET /admin/users ────────────────────────────────────────
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<User> userPage = userRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending()));

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        return "admin/user/list";
    }

    // ─── POST /admin/users/{id}/toggle-status ────────────────────
    // Khoá / mở tài khoản
    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (user.getStatusUser() == AppEnums.UserStatus.ACTIVE) {
            user.setStatusUser(AppEnums.UserStatus.INACTIVE);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã khoá tài khoản: " + user.getEmail());
        } else {
            user.setStatusUser(AppEnums.UserStatus.ACTIVE);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã mở tài khoản: " + user.getEmail());
        }

        userRepository.save(user);
        return "redirect:/admin/users";
    }
}