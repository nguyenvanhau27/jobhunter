package com.jobhunter.jobhunter.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String experience;
    private Integer statusUser;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}