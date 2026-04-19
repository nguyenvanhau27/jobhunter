package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.dto.AdminUserDTO;
import com.jobhunter.jobhunter.entity.Role;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.RoleRepository;
import com.jobhunter.jobhunter.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final int PAGE_SIZE = 5;

    private final AdminUserService adminUserService;
    private final RoleRepository roleRepository;

    public AdminUserController(AdminUserService adminUserService,
                               RoleRepository roleRepository) {
        this.adminUserService = adminUserService;
        this.roleRepository = roleRepository;
    }

    // ── List ─────────────────────────────────────────
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {
        Page<User> userPage = adminUserService.listUsers(keyword, page, PAGE_SIZE);
        model.addAttribute("userPage",    userPage);
        model.addAttribute("users",       userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword",     keyword);
        model.addAttribute("now",         LocalDateTime.now());
        return "admin/user/list";
    }

    // ── Detail / Edit form ────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User user = adminUserService.findById(id);
        List<Role> roles = roleRepository.findAll();

        AdminUserDTO dto = new AdminUserDTO();
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setExperience(user.getExperience());
        dto.setRoleName(user.getRole() != null ? user.getRole().getName() : "USER");

        model.addAttribute("user",  user);
        model.addAttribute("dto",   dto);
        model.addAttribute("roles", roles);
        model.addAttribute("now",   LocalDateTime.now());
        return "admin/user/detail";
    }

    // ── Update user ───────────────────────────────────
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("dto") AdminUserDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        // Bỏ lỗi @Size của newPassword nếu admin để trống (optional field)
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            bindingResult = dropFieldError(bindingResult, dto, "newPassword");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user",  adminUserService.findById(id));
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("now",   LocalDateTime.now());
            return "admin/user/detail";
        }

        try {
            adminUserService.updateUser(id, dto);
            ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    // ── Toggle status (gọi từ list hoặc detail) ──────
    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable Long id,
            @RequestParam(defaultValue = "list") String from,
            RedirectAttributes ra) {

        User updated = adminUserService.toggleStatus(id);
        boolean isActive = updated.getStatusUser().toString().equals("ACTIVE");
        ra.addFlashAttribute("successMessage",
                isActive ? "Đã mở khoá: " + updated.getEmail()
                        : "Đã khoá: " + updated.getEmail());

        return "detail".equals(from)
                ? "redirect:/admin/users/" + id
                : "redirect:/admin/users";
    }

    /** Tạo BindingResult mới bỏ lỗi của field chỉ định */
    private BindingResult dropFieldError(BindingResult br, Object target, String field) {
        BeanPropertyBindingResult cleaned = new BeanPropertyBindingResult(target, "dto");
        br.getAllErrors().forEach(e -> {
            if (e instanceof FieldError fe && field.equals(fe.getField())) return;
            cleaned.addError(e);
        });
        return cleaned;
    }
}