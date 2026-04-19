package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.repository.SkillRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.repository.UserSkillRepository;
import com.jobhunter.jobhunter.service.UserSkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSkillServiceImpl implements UserSkillService {

    private static final Logger log = LoggerFactory.getLogger(UserSkillServiceImpl.class);

    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public UserSkillServiceImpl(UserSkillRepository userSkillRepository,
                                SkillRepository skillRepository,
                                UserRepository userRepository) {
        this.userSkillRepository = userSkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserSkill> getSkillsByUserId(Long userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @Override
    public List<UserSkill> searchSkills(Long userId, String searchName, String searchCategory) {
        List<UserSkill> all = userSkillRepository.findByUserId(userId);

        boolean hasName = searchName != null && !searchName.isBlank();
        boolean hasCat  = searchCategory != null && !searchCategory.isBlank();

        if (!hasName && !hasCat) return all;

        return all.stream()
                .filter(us -> {
                    if (us.getSkill() == null) return false;

                    if (hasName) {
                        String name = us.getSkill().getName() != null
                                ? us.getSkill().getName() : "";
                        if (!name.toLowerCase().contains(searchName.toLowerCase().trim()))
                            return false;
                    }
                    if (hasCat) {
                        String cat = us.getSkill().getCategory() != null
                                ? us.getSkill().getCategory() : "";
                        if (!searchCategory.equals(cat)) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addSkill(Long userId, Long skillId, AppEnums.SkillLevel level) {
        if (userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new IllegalArgumentException("Bạn đã có skill này");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill không tồn tại"));

        UserSkill us = new UserSkill();
        us.setUser(user);
        us.setSkill(skill);
        us.setLevelSkill(level);
        userSkillRepository.save(us);

        log.info("SKILL ADDED | userId={} | skillId={} | level={}", userId, skillId, level);
    }

    @Override
    @Transactional
    public void updateSkillLevel(Long userId, Long skillId, AppEnums.SkillLevel level) {
        UserSkill us = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy skill này trong danh sách của bạn"));
        us.setLevelSkill(level);
        userSkillRepository.save(us);

        log.info("SKILL UPDATED | userId={} | skillId={} | level={}", userId, skillId, level);
    }

    @Override
    @Transactional
    public void removeSkill(Long userId, Long skillId) {
        if (!userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new RuntimeException("Không tìm thấy skill này trong danh sách của bạn");
        }
        userSkillRepository.deleteByUserIdAndSkillId(userId, skillId);

        log.info("SKILL REMOVED | userId={} | skillId={}", userId, skillId);
    }
}