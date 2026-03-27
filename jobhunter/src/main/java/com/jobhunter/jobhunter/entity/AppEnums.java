package com.jobhunter.jobhunter.entity;

public class AppEnums {

    // Tạo file JobType.java
    public enum JobType { FULLTIME, PART_TIME, INTERN, REMOTE }

    // Tạo file ExperienceLevel.java
    public enum ExperienceLevel { JUNIOR, SENIOR, MID }

    // Tạo file ApplicationStatus.java
    public enum ApplicationStatus { PENDING, APPROVED, REJECTED }

    // Tạo file UserStatus.java
    public enum UserStatus { ACTIVE, INACTIVE }

    // Tạo file JobStatus.java
    public enum JobStatus { OPEN, CLOSED }

    public enum SkillLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
