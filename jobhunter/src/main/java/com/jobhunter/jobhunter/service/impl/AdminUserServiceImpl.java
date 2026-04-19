package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.AdminUserDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Role;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.RoleRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> listUsers(String keyword, int page, int pageSize) {
        var pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return (kw != null)
                ? userRepository.searchUsers(kw, pageable)
                : userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại: " + id));
    }

    @Override
    @Transactional
    public User updateUser(Long id, AdminUserDTO dto) {
        User user = findById(id);

        // Cập nhật thông tin cá nhân
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setExperience(dto.getExperience());

        // Cập nhật role
        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role không hợp lệ: " + dto.getRoleName()));
        user.setRole(role);

        // Đổi mật khẩu nếu admin nhập mật khẩu mới (không bắt buộc)
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (dto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            log.info("ADMIN RESET PASSWORD | userId={} | email={}", id, user.getEmail());
        }

        User saved = userRepository.save(user);
        log.info("ADMIN UPDATE USER | userId={} | email={} | role={}",
                id, user.getEmail(), dto.getRoleName());
        return saved;
    }

    @Override
    @Transactional
    public User toggleStatus(Long id) {
        User user = findById(id);
        boolean wasActive = user.getStatusUser() == AppEnums.UserStatus.ACTIVE;
        user.setStatusUser(wasActive
                ? AppEnums.UserStatus.INACTIVE
                : AppEnums.UserStatus.ACTIVE);
        User saved = userRepository.save(user);
        log.info("ADMIN TOGGLE STATUS | userId={} | email={} | newStatus={}",
                id, user.getEmail(), saved.getStatusUser());
        return saved;
    }
}