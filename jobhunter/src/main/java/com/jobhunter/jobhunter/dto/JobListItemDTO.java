package com.jobhunter.jobhunter.dto;

import java.time.LocalDateTime;

public class JobListItemDTO {

    private Long          id;
    private String        title;
    private String        companyName;
    private String        statusJob;           // String (từ JPQL CAST)
    private LocalDateTime expiredAt;
    private LocalDateTime updatedAt;
    private long          candidateCount;      // tổng số đơn
    private long          pendingCount;        // số đơn PENDING (dùng cho notification dot)

    // ── Constructor cho JPQL constructor expression ──────────────
    public JobListItemDTO(Long id, String title, String companyName,
                          String statusJob,
                          LocalDateTime expiredAt, LocalDateTime updatedAt,
                          long candidateCount, long pendingCount) {
        this.id             = id;
        this.title          = title;
        this.companyName    = companyName;
        this.statusJob      = statusJob;
        this.expiredAt      = expiredAt;
        this.updatedAt      = updatedAt;
        this.candidateCount = candidateCount;
        this.pendingCount   = pendingCount;
    }

    public Long          getId()             { return id; }
    public String        getTitle()          { return title; }
    public String        getCompanyName()    { return companyName; }
    public String        getStatusJob()      { return statusJob; }
    public LocalDateTime getExpiredAt()      { return expiredAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public long          getCandidateCount() { return candidateCount; }
    public long          getPendingCount()   { return pendingCount; }
}