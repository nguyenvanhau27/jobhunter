package com.jobhunter.jobhunter.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "description_job", columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private Integer minSalary;
    private Integer maxSalary;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private Enum.JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private Enum.Experience experienceLevel;

    private Integer statusJob;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
