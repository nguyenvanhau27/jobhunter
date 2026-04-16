package com.jobhunter.jobhunter.scheduler;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobScheduler {

    private final JobRepository jobRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public JobScheduler(JobRepository jobRepository,
                        PasswordResetTokenRepository tokenRepository) {
        this.jobRepository = jobRepository;
        this.tokenRepository = tokenRepository;
    }

    // Auto CLOSED job expired — run at 00:00
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredJobs() {
        List<Job> expiredJobs = jobRepository.findExpiredOpenJobs(LocalDateTime.now());

        if (expiredJobs.isEmpty()) {
            System.out.println("[Scheduler] No expired jobs found.");
            return;
        }

        for (Job job : expiredJobs) {
            job.setStatusJob(AppEnums.JobStatus.CLOSED);
            jobRepository.save(job);
            System.out.println("[Scheduler] CLOSED expired job: [" + job.getId() + "] " + job.getTitle());
        }

        System.out.println("[Scheduler] Total closed: " + expiredJobs.size() + " jobs.");
    }

    // Clear expired tokens and reset passwords — runs daily at 01:00
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cleanExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        System.out.println("[Scheduler] Cleaned expired password reset tokens.");
    }
}