package com.jobhunter.jobhunter.dto;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.userSkill.UserSkill;
import java.time.LocalDateTime;
import java.util.List;

public class ApplicationDetailDTO {

    // Application fields
    private Long   appId;
    private String status;          // "PENDING" / "APPROVED" / "REJECTED"
    private LocalDateTime appliedAt;
    private String cvFile;
    private String coverLetter;

    // User fields (flat — no entity reference)
    private Long   userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;

    // Skills (list of UserSkill — already loaded by service)
    private List<UserSkill> userSkills;

    // Matching percent (computed)
    private int matchingPercent;

    // ── Constructor ───────────────────────────────────────────────
    public ApplicationDetailDTO() {}

    // ── Getters / Setters ─────────────────────────────────────────
    public Long   getAppId()          { return appId; }
    public void   setAppId(Long v)    { this.appId = v; }

    public String getStatus()         { return status; }
    public void   setStatus(String v) { this.status = v; }

    public LocalDateTime getAppliedAt()           { return appliedAt; }
    public void          setAppliedAt(LocalDateTime v) { this.appliedAt = v; }

    public String getCvFile()         { return cvFile; }
    public void   setCvFile(String v) { this.cvFile = v; }

    public String getCoverLetter()         { return coverLetter; }
    public void   setCoverLetter(String v) { this.coverLetter = v; }

    public Long   getUserId()         { return userId; }
    public void   setUserId(Long v)   { this.userId = v; }

    public String getFullName()         { return fullName; }
    public void   setFullName(String v) { this.fullName = v; }

    public String getEmail()         { return email; }
    public void   setEmail(String v) { this.email = v; }

    public String getPhone()         { return phone; }
    public void   setPhone(String v) { this.phone = v; }

    public String getAddress()         { return address; }
    public void   setAddress(String v) { this.address = v; }

    public List<UserSkill> getUserSkills()              { return userSkills; }
    public void            setUserSkills(List<UserSkill> v) { this.userSkills = v; }

    public int  getMatchingPercent()      { return matchingPercent; }
    public void setMatchingPercent(int v) { this.matchingPercent = v; }
}