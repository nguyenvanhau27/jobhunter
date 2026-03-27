package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.entity.userSkill.UserSkillId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {

    @EntityGraph(attributePaths = {"skill"})  // ← thêm dòng này
    List<UserSkill> findByUserId(Long userId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    @Transactional
    void deleteByUserIdAndSkillId(Long userId, Long skillId);
}