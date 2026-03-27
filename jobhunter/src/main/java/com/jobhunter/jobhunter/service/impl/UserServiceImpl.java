package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.ProfileDTO;
import com.jobhunter.jobhunter.dto.RegisterDTO;
import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Role;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.RoleRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterDTO dto) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }

        // Lấy role USER từ DB
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại trong DB"));

        // Tạo user mới
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(userRole);
        user.setStatusUser(AppEnums.UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    @Override
    public User updateProfile(String email, ProfileDTO dto) {
        // Lấy user từ email của người đang đăng nhập
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Chỉ cập nhật các field được phép — không động đến email, password, role
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setExperience(dto.getExperience());

        User saved = userRepository.save(user);

        System.out.println("=================================");
        System.out.println("PROFILE UPDATED");
        System.out.println("Email    : " + email);
        System.out.println("FullName : " + dto.getFullName());
        System.out.println("=================================");

        return saved;
    }
}