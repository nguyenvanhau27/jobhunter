package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final int PAGE_SIZE = 5;
    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("createdAt").descending());

        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<User> userPage = (kw != null)
                ? userRepository.searchUsers(kw, pageable)
                : userRepository.findAll(pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "admin/user/list";
    }


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