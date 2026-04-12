package com.jobhunter.jobhunter.dto;

import com.jobhunter.jobhunter.entity.Application;
import java.time.LocalDateTime;

public class ApplicationListItemDTO {

    private Long          id;
    private String        applicantName;
    private String        applicantEmail;
    private String        applicantPhone;
    private String        applicantAddress;
    private LocalDateTime appliedAt;
    private String        status;             // PENDING / APPROVED / REJECTED
    private int           matchingPercent;    // 0-100
    private String        cvFile;
    private String        coverLetter;

    // ── Constructor từ Application entity + computed matching % ──
    public ApplicationListItemDTO(Application app, int matchingPercent) {
        this.id               = app.getId();
        this.applicantName    = app.getUser().getFullName();
        this.applicantEmail   = app.getUser().getEmail();
        this.applicantPhone   = app.getUser().getPhone();       // nếu User có field phone
        this.applicantAddress = app.getUser().getAddress();     // nếu User có field address
        this.appliedAt        = app.getAppliedAt();
        this.status           = app.getStatus().name();
        this.matchingPercent  = matchingPercent;
        this.cvFile           = app.getCvFile();
        this.coverLetter      = app.getCoverLetter();
    }

    public Long          getId()               { return id; }
    public String        getApplicantName()    { return applicantName; }
    public String        getApplicantEmail()   { return applicantEmail; }
    public String        getApplicantPhone()   { return applicantPhone; }
    public String        getApplicantAddress() { return applicantAddress; }
    public LocalDateTime getAppliedAt()        { return appliedAt; }
    public String        getStatus()           { return status; }
    public int           getMatchingPercent()  { return matchingPercent; }
    public String        getCvFile()           { return cvFile; }
    public String        getCoverLetter()      { return coverLetter; }
}

