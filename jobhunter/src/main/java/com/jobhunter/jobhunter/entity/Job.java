package com.jobhunter.jobhunter.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "description_job", columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "min_salary")
    private Long minSalary;

    @Column(name = "max_salary")
    private Long maxSalary;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private AppEnums.JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private AppEnums.ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_job")
    private AppEnums.JobStatus statusJob;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    public Job() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.statusJob == null) {
            this.statusJob = AppEnums.JobStatus.OPEN;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public Long getMinSalary() { return minSalary; }
    public void setMinSalary(Long minSalary) { this.minSalary = minSalary; }

    public Long getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Long maxSalary) { this.maxSalary = maxSalary; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public AppEnums.JobType getJobType() { return jobType; }
    public void setJobType(AppEnums.JobType jobType) { this.jobType = jobType; }

    public AppEnums.ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(AppEnums.ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }

    public AppEnums.JobStatus getStatusJob() { return statusJob; }
    public void setStatusJob(AppEnums.JobStatus statusJob) { this.statusJob = statusJob; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Set<Skill> getSkills() { return skills; }
    public void setSkills(Set<Skill> skills) { this.skills = skills; }

    @Override
    public String toString() {
        return "Job{id=" + id + ", title='" + title + "'}";
    }
}