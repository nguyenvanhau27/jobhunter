package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.entity.userSkill.UserSkillId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {

    @EntityGraph(attributePaths = {"skill"})
    List<UserSkill> findByUserId(Long userId);

    // Thêm phương thức tìm kiếm kết hợp keyword và category
    @EntityGraph(attributePaths = {"skill"})
    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId " +
            "AND (:category IS NULL OR :category = '' OR us.skill.category = :category) " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(us.skill.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<UserSkill> searchMySkills(Long userId, String keyword, String category);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    @Transactional
    void deleteByUserIdAndSkillId(Long userId, Long skillId);
}