package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.PasswordResetToken;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.PasswordResetTokenRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.reset-token-expiry-minutes}")
    private int expiryMinutes;

    public PasswordService(UserRepository userRepository,
                           PasswordResetTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    // ─── UC: Đổi password (đã đăng nhập) ───────────────────────
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Xác nhận password cũ đúng không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        System.out.println("=================================");
        System.out.println("PASSWORD CHANGED");
        System.out.println("Email : " + email);
        System.out.println("=================================");
    }

    // ─── UC: Quên password — gửi email reset ────────────────────
    public void sendResetEmail(String email) {
        // Kiểm tra email tồn tại — không thông báo lỗi ra ngoài (bảo mật)
        userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Email không tồn tại trong hệ thống")
        );

        // Xoá token cũ nếu có
        tokenRepository.deleteByEmail(email);

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
        tokenRepository.save(resetToken);

        // Gửi email
        String resetLink = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[JobHunter] Đặt lại mật khẩu");
        message.setText(
                "Xin chào,\n\n" +
                        "Bạn đã yêu cầu đặt lại mật khẩu.\n\n" +
                        "Click vào link bên dưới để đặt lại mật khẩu (hiệu lực " + expiryMinutes + " phút):\n\n" +
                        resetLink + "\n\n" +
                        "Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.\n\n" +
                        "JobHunter Team"
        );
        mailSender.send(message);

        System.out.println("=================================");
        System.out.println("RESET EMAIL SENT");
        System.out.println("Email : " + email);
        System.out.println("Token : " + token);
        System.out.println("Expiry: " + expiryMinutes + " minutes");
        System.out.println("=================================");
    }

    // ─── UC: Đặt lại password bằng token ────────────────────────
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

        // Cập nhật password
        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Đánh dấu token đã dùng
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        System.out.println("=================================");
        System.out.println("PASSWORD RESET SUCCESS");
        System.out.println("Email : " + resetToken.getEmail());
        System.out.println("=================================");
    }
}