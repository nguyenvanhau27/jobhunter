package com.jobhunter.jobhunter.security;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy user: " + email));

        if (user.getStatusUser() == AppEnums.UserStatus.INACTIVE) {
            throw new DisabledException("Tài khoản đã bị khóa");
        }

        boolean enabled = user.getStatusUser() == AppEnums.UserStatus.ACTIVE;
        String roleWithPrefix = "ROLE_" + user.getRole().getName();

        // Cập nhật lastLogin đúng nơi: service security, không phải config
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        log.info("LOGIN | email={} | role={} | enabled={}", email, roleWithPrefix, enabled);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(roleWithPrefix)))
                .disabled(!enabled)
                .build();
    }
}