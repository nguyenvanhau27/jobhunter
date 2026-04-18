package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;

import java.util.List;
import java.util.Map;

public interface UserSkillService {

    List<com.jobhunter.jobhunter.entity.userSkill.UserSkill> getSkillsByUserId(Long userId);
    // Phương thức trả về một Object chứa cả danh sách và thông tin phân trang
    List<UserSkill> searchSkills(Long userId, String searchName, String searchCategory);

    // Thêm skill
    void addSkill(Long userId, Long skillId, AppEnums.SkillLevel level);

    // Cập nhật level
    void updateSkillLevel(Long userId, Long skillId, AppEnums.SkillLevel level);

    // Xoá skill
    void removeSkill(Long userId, Long skillId);
}