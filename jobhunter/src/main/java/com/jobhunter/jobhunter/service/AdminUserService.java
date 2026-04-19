package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.AdminUserDTO;
import com.jobhunter.jobhunter.entity.User;
import org.springframework.data.domain.Page;

/**
 * Service cho admin quản lý user.
 * Tách riêng với UserService (dành cho user tự quản lý profile).
 */
public interface AdminUserService {

    /** Danh sách user có search + phân trang */
    Page<User> listUsers(String keyword, int page, int pageSize);

    /** Lấy 1 user theo id */
    User findById(Long id);

    /**
     * Admin cập nhật thông tin user:
     * - Thông tin cá nhân (fullName, phone, address, experience)
     * - Role (USER / ADMIN)
     * - Mật khẩu mới (nếu newPassword không blank)
     */
    User updateUser(Long id, AdminUserDTO dto);

    /** Khoá hoặc mở khoá tài khoản */
    User toggleStatus(Long id);
}