package com.jobhunter.jobhunter.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_company")
    private String nameCompany;

    @Column(name = "description_company", columnDefinition = "TEXT")
    private String description;

    private String size;
    private String location;

    @Column(name = "image_url")
    private String imageUrl;

    private String website;

    private LocalDateTime createdAt;

}
