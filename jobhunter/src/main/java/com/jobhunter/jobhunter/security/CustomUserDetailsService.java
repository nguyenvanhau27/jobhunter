package com.jobhunter.jobhunter.security;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security dùng class này để load User từ DB khi login.
 * Phải đăng ký bean này vào SecurityConfig.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + email));

        // Kiểm tra tài khoản có bị khoá không
        boolean enabled = user.getStatusUser() == AppEnums.UserStatus.ACTIVE;


        // Role lưu trong DB là "USER" / "ADMIN"
        // Spring Security yêu cầu prefix "ROLE_" → "ROLE_USER" / "ROLE_ADMIN"
        String roleWithPrefix = "ROLE_" + user.getRole().getName();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(roleWithPrefix)))
                .disabled(!enabled)
                .build();
    }
}