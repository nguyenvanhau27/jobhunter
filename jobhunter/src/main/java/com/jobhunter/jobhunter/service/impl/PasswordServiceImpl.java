package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.PasswordResetToken;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.PasswordResetTokenRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordServiceImpl implements PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.reset-token-expiry-minutes}")
    private int expiryMinutes;

    public PasswordServiceImpl(UserRepository userRepository,
                               PasswordResetTokenRepository tokenRepository,
                               PasswordEncoder passwordEncoder,
                               JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("PASSWORD CHANGED | email={}", email);
    }

    @Override
    @Transactional
    public void sendResetEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Email không tồn tại trong hệ thống"));

        // Xoá token cũ nếu có
        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
        tokenRepository.save(resetToken);

        String resetLink = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[JobHunter] Đặt lại mật khẩu");
        message.setText(
                "Xin chào,\n\n"
                        + "Bạn đã yêu cầu đặt lại mật khẩu.\n\n"
                        + "Click vào link bên dưới để đặt lại mật khẩu (hiệu lực "
                        + expiryMinutes + " phút):\n\n"
                        + resetLink + "\n\n"
                        + "Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.\n\n"
                        + "JobHunter Team"
        );
        mailSender.send(message);
        log.info("RESET EMAIL SENT | email={} | expiry={}m", email, expiryMinutes);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token đã được sử dụng");
        }
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token đã hết hạn. Vui lòng yêu cầu lại.");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        log.info("PASSWORD RESET SUCCESS | email={}", resetToken.getEmail());
    }
}