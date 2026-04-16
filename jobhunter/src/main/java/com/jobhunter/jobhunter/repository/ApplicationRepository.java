package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUser_IdAndJob_Id(Long userId, Long jobId);

    List<Application> findByUser_Id(Long userId);

    List<Application> findByJobId(Long jobId);

    boolean existsByJobIdAndStatus(Long jobId, AppEnums.ApplicationStatus status);

    long countByStatus(AppEnums.ApplicationStatus status);

    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);

    // ── Eager load user + userSkills + skill to detail page ──
    // EntityGraph avoid LazyInitializationException và N+1
    @EntityGraph(attributePaths = {
            "user",
            "user.userSkills",
            "user.userSkills.skill"
    })
    Optional<Application> findWithUserById(Long id);

    // ── Eager load list follow jobId ──
    @EntityGraph(attributePaths = {
            "user",
            "user.userSkills",
            "user.userSkills.skill"
    })
    List<Application> findWithUserByJobId(Long jobId);

}

