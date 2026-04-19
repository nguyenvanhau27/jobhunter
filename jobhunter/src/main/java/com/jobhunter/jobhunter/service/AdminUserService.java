package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.AdminUserDTO;
import com.jobhunter.jobhunter.entity.User;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<User> listUsers(String keyword, int page, int pageSize);

    User findById(Long id);

    User updateUser(Long id, AdminUserDTO dto);

    User toggleStatus(Long id);
}