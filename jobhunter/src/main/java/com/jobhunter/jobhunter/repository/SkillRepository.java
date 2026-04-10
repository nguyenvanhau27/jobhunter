package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByName(String name);

    List<Skill> findByCategory(String category);

    List<Skill> findAllByOrderByCategoryAscNameAsc();

    // ── THÊM MỚI ──────────────────────────────────────────────

    // Lấy tất cả category không trùng (để hiển thị dropdown filter)
    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category ASC")
    List<String> findAllCategories();

    // Tổng số skill
    // (đã có sẵn count() từ JpaRepository)

    // Search theo tên hoặc category, có phân trang, sort theo id giảm dần
    @Query("SELECT s FROM Skill s WHERE " +
            "(:keyword  IS NULL OR LOWER(s.name)     LIKE LOWER(CONCAT('%', :keyword,  '%'))) AND " +
            "(:category IS NULL OR LOWER(s.category) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Skill> searchSkills(@Param("keyword")  String keyword,
                             @Param("category") String category,
                             Pageable pageable);
}