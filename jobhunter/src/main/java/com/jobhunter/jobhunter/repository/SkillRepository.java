package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByName(String name);

    List<Skill> findByCategory(String category);

    List<Skill> findAllByOrderByCategoryAscNameAsc();
}