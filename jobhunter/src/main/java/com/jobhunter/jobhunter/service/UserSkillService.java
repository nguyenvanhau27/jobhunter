package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;

import java.util.List;

public interface UserSkillService {

    // Lấy danh sách skill của user
    List<com.jobhunter.jobhunter.entity.userSkill.UserSkill> getSkillsByUserId(Long userId);

    // Thêm skill
    void addSkill(Long userId, Long skillId, AppEnums.SkillLevel level);

    // Cập nhật level
    void updateSkillLevel(Long userId, Long skillId, AppEnums.SkillLevel level);

    // Xoá skill
    void removeSkill(Long userId, Long skillId);
}