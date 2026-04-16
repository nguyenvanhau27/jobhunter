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

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    public Job() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.statusJob == null) this.statusJob = AppEnums.JobStatus.OPEN;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expiredAt != null && LocalDateTime.now().isAfter(this.expiredAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String r) {
        this.requirements = r;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long v) {
        this.minSalary = v;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long v) {
        this.maxSalary = v;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String v) {
        this.location = v;
    }

    public AppEnums.JobType getJobType() {
        return jobType;
    }

    public void setJobType(AppEnums.JobType v) {
        this.jobType = v;
    }

    public AppEnums.ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(AppEnums.ExperienceLevel v) {
        this.experienceLevel = v;
    }

    public AppEnums.JobStatus getStatusJob() {
        return statusJob;
    }

    public void setStatusJob(AppEnums.JobStatus v) {
        this.statusJob = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime v) {
        this.expiredAt = v;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime v) {
        this.updatedAt = v;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company v) {
        this.company = v;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> v) {
        this.skills = v;
    }
}