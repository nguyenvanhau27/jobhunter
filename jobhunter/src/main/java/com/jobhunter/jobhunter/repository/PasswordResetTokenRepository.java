package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.email = :email")
    void deleteByEmail(@Param("email") String email);

    // Scheduler: xoá token hết hạn hoặc đã dùng
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryTime < :now OR t.used = true")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}