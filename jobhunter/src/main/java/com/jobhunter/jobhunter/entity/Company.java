package com.jobhunter.jobhunter.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_company", nullable = false)
    private String nameCompany;

    @Column(name = "description_company", columnDefinition = "TEXT")
    private String description;

    private String size;
    private String location;

    @Column(name = "image_url")
    private String imageUrl;

    private String website;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // FIX #9: Thêm field updatedAt — nhất quán với Job và Application
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Company() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // FIX #9: Thêm @PreUpdate
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String v) {
        this.nameCompany = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String v) {
        this.size = v;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String v) {
        this.location = v;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String v) {
        this.imageUrl = v;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String v) {
        this.website = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime v) {
        this.updatedAt = v;
    }

    @Override
    public String toString() {
        return "Company{id=" + id + ", nameCompany='" + nameCompany + "'}";
    }
}