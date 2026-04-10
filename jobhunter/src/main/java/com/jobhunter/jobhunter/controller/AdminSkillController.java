package com.jobhunter.jobhunter.controller;

import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private static final int PAGE_SIZE = 20;

    private final SkillRepository skillRepository;

    public AdminSkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // ─── GET /admin/skills ───────────────────────────────────────
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(required = false)    String keyword,
            @RequestParam(required = false)    String category,
            Model model) {

        String kw  = (keyword  != null && !keyword.isBlank())  ? keyword.trim()  : null;
        String cat = (category != null && !category.isBlank()) ? category.trim() : null;

        Pageable pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("id").descending());

        Page<Skill> skillPage = skillRepository.searchSkills(kw, cat, pageable);

        model.addAttribute("skillPage",    skillPage);
        model.addAttribute("skills",       skillPage.getContent());
        model.addAttribute("currentPage",  page);
        model.addAttribute("keyword",      keyword);
        model.addAttribute("category",     category);
        model.addAttribute("allCategories", skillRepository.findAllCategories());
        model.addAttribute("totalSkills",  skillRepository.count());

        return "admin/skill/list";
    }

    // ─── POST /admin/skills/add ──────────────────────────────────
    // Thêm skill mới (inline form trong trang list)
    @PostMapping("/add")
    public String add(
            @RequestParam String name,
            @RequestParam String category,
            RedirectAttributes redirectAttributes) {

        name     = name.trim();
        category = category.trim();

        if (name.isEmpty() || category.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Tên skill và category không được để trống.");
            return "redirect:/admin/skills";
        }
        if (skillRepository.existsByName(name)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Skill \"" + name + "\" đã tồn tại.");
            return "redirect:/admin/skills";
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        skillRepository.save(skill);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã thêm skill \"" + name + "\" vào category " + category);
        return "redirect:/admin/skills";
    }

    // ─── POST /admin/skills/{id}/update ──────────────────────────
    // Inline edit tên + category
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String category,
            RedirectAttributes redirectAttributes) {

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill không tồn tại: " + id));

        skill.setName(name.trim());
        skill.setCategory(category.trim());
        skillRepository.save(skill);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã cập nhật skill #" + id);
        return "redirect:/admin/skills";
    }

    // ─── POST /admin/skills/{id}/delete ──────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            skillRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xoá skill #" + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xoá skill này (có thể đang được sử dụng bởi một Job).");
        }
        return "redirect:/admin/skills";
    }
}