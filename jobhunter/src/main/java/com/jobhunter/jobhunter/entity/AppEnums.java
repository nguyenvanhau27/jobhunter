package com.jobhunter.jobhunter.entity;

public class AppEnums {
    /**
     * Loại hình công việc
     */
    public enum JobType {
        FULLTIME, PART_TIME, INTERN, REMOTE
    }

    /**
     * Cấp độ kinh nghiệm yêu cầu
     */
    public enum ExperienceLevel {
        JUNIOR, MID, SENIOR
    }

    /**
     * Trạng thái đơn ứng tuyển
     */
    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    /**
     * Trạng thái tài khoản người dùng
     */
    public enum UserStatus {
        ACTIVE, INACTIVE
    }

    /**
     * Trạng thái tin tuyển dụng
     */
    public enum JobStatus {
        OPEN, CLOSED
    }

    /**
     * Cấp độ kỹ năng của người dùng
     */
    public enum SkillLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

}
//    }
//    // Create file JobType.java
//    public enum JobType {FULLTIME, PART_TIME, INTERN, REMOTE}
//
//    // Create file ExperienceLevel.java
//    public enum ExperienceLevel {JUNIOR, MID, SENIOR}
//
//    // Create file ApplicationStatus.java
//    public enum ApplicationStatus {PENDING, APPROVED, REJECTED}
//
//    // Create file UserStatus.java
//    public enum UserStatus {ACTIVE, INACTIVE}
//
//    // Create file JobStatus.java
//    public enum JobStatus {OPEN, CLOSED}
//
//    public enum SkillLevel {
//        BEGINNER, INTERMEDIATE, ADVANCED
//    }
//}
