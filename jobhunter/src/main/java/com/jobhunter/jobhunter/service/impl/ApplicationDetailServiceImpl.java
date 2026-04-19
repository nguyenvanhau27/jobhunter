package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.ApplicationDetailDTO;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.repository.UserSkillRepository;
import com.jobhunter.jobhunter.service.ApplicationDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class ApplicationDetailServiceImpl implements ApplicationDetailService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationDetailServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public ApplicationDetailServiceImpl(ApplicationRepository applicationRepository,
                                        JobRepository jobRepository,
                                        UserRepository userRepository,
                                        UserSkillRepository userSkillRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Override
    public ApplicationDetailDTO getDetail(Long jobId, Long appId) {

        // Load job (cần skills để tính matching)
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại: " + jobId));

        Set<Long> jobSkillIds = job.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        // Load application (scalar fields)
        Application application = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Đơn ứng tuyển không tồn tại: " + appId));

        // Load user
        Long userId = application.getUserId();
        User user = userId != null
                ? userRepository.findById(userId).orElse(null)
                : null;

        // Load userSkills
        List<UserSkill> userSkills = user != null
                ? userSkillRepository.findByUserId(user.getId())
                : Collections.emptyList();

        // Tính matching %
        int matchingPct = 0;
        if (!jobSkillIds.isEmpty() && !userSkills.isEmpty()) {
            long common = userSkills.stream()
                    .filter(us -> us.getSkill() != null
                            && jobSkillIds.contains(us.getSkill().getId()))
                    .count();
            matchingPct = (int) Math.round((double) common / jobSkillIds.size() * 100);
        }

        log.debug("ApplicationDetail | appId={} | matching={}%", appId, matchingPct);

        // Build DTO — flat, không expose entity ra controller
        ApplicationDetailDTO dto = new ApplicationDetailDTO();
        dto.setAppId(application.getId());
        dto.setStatus(application.getStatus() != null
                ? application.getStatus().name() : "PENDING");
        dto.setAppliedAt(application.getAppliedAt());
        dto.setCvFile(application.getCvFile());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setMatchingPercent(matchingPct);
        dto.setUserSkills(userSkills);

        if (user != null) {
            dto.setUserId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
        }

        return dto;
    }
}