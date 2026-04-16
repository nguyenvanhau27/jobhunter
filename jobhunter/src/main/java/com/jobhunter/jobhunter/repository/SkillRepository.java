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

    // Get all category duplicate (to show dropdown filter)
    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category ASC")
    List<String> findAllCategories();

    // Search by name or category, have pagination, sort by id
    @Query("SELECT s FROM Skill s WHERE " +
            "(:keyword  IS NULL OR LOWER(s.name)     LIKE LOWER(CONCAT('%', :keyword,  '%'))) AND " +
            "(:category IS NULL OR LOWER(s.category) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Skill> searchSkills(@Param("keyword") String keyword,
                             @Param("category") String category,
                             Pageable pageable);
}