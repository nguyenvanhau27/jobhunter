package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final SkillRepository skillRepository;

    public AdminSkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // ─── GET /admin/skills ───────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("skills", skillRepository.findAll());
        return "admin/skill/list";
    }

    // ─── POST /admin/skills/create ───────────────────────────────
    @PostMapping("/create")
    public String create(
            @RequestParam String name,
            @RequestParam(required = false) String category,
            RedirectAttributes redirectAttributes) {

        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên skill không được để trống");
            return "redirect:/admin/skills";
        }

        if (skillRepository.existsByName(name.trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Skill \"" + name + "\" đã tồn tại");
            return "redirect:/admin/skills";
        }

        Skill skill = new Skill();
        skill.setName(name.trim());
        skill.setCategory(category);
        skillRepository.save(skill);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm skill thành công!");
        return "redirect:/admin/skills";
    }

    // ─── POST /admin/skills/{id}/delete ─────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        skillRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá skill!");
        return "redirect:/admin/skills";
    }

    // ─── POST /admin/skills/{id}/edit ───────────────────────────
    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String category,
            RedirectAttributes redirectAttributes) {

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill không tồn tại"));

        skill.setName(name.trim());
        skill.setCategory(category);
        skillRepository.save(skill);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật skill thành công!");
        return "redirect:/admin/skills";
    }
}