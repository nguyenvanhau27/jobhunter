package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.repository.UserSkillRepository;
import com.jobhunter.jobhunter.service.UserSkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillServiceImpl(UserSkillRepository userSkillRepository,
                                UserRepository userRepository,
                                SkillRepository skillRepository) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public List<UserSkill> getSkillsByUserId(Long userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @Override
    public void addSkill(Long userId, Long skillId, AppEnums.SkillLevel level) {
        // E1: Kiểm tra skill đã tồn tại chưa
        if (userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new IllegalArgumentException("Bạn đã có skill này");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill không tồn tại"));

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setLevelSkill(level);

        userSkillRepository.save(userSkill);

        System.out.println("=================================");
        System.out.println("SKILL ADDED");
        System.out.println("UserId  : " + userId);
        System.out.println("Skill   : " + skill.getName());
        System.out.println("Level   : " + level);
        System.out.println("=================================");
    }

    @Override
    public void updateSkillLevel(Long userId, Long skillId, AppEnums.SkillLevel level) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy skill này trong danh sách của bạn"));

        userSkill.setLevelSkill(level);
        userSkillRepository.save(userSkill);

        System.out.println("=================================");
        System.out.println("SKILL LEVEL UPDATED");
        System.out.println("UserId  : " + userId);
        System.out.println("SkillId : " + skillId);
        System.out.println("Level   : " + level);
        System.out.println("=================================");
    }

    @Override
    @Transactional
    public void removeSkill(Long userId, Long skillId) {
        if (!userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new RuntimeException("Không tìm thấy skill này trong danh sách của bạn");
        }
        userSkillRepository.deleteByUserIdAndSkillId(userId, skillId);

        System.out.println("=================================");
        System.out.println("SKILL REMOVED");
        System.out.println("UserId  : " + userId);
        System.out.println("SkillId : " + skillId);
        System.out.println("=================================");
    }
}
