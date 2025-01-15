/*
 * TraderX - A trading automation software.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.userservice.service;

import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.model.VerificationToken;
import com.smoothstack.userservice.repository.AppUserRepository;
import com.smoothstack.userservice.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final long RESEND_COOLDOWN_PERIOD = Duration.of(10, ChronoUnit.MINUTES).toMillis();

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
//    private final EmailService emailService;

    public void initiatePasswordReset(String emailOrUsername) {
        logger.info("Initiating password reset for: {}", emailOrUsername);
        AppUser user = appUserRepository.findByEmail(emailOrUsername)
                .or(() -> appUserRepository.findByUsername(emailOrUsername))
                .orElseThrow(() -> {
                    logger.error("User not found with email or username: {}", emailOrUsername);
                    return new EntityNotFoundException("User not found with email or username: " + emailOrUsername);
                });

        List<VerificationToken> tokens = jdbcTemplate.query(
                "SELECT * FROM password_reset_token WHERE user_id = ? ORDER BY last_verification_request DESC LIMIT 1",
                new Object[]{user.getUserId()},
                (rs, rowNum) -> new VerificationToken(
                        rs.getInt("token_id"),
                        rs.getInt("user_id"),
                        rs.getString("token"),
                        rs.getTimestamp("expiration"),
                        rs.getBoolean("is_used"),
                        rs.getTimestamp("last_verification_request")
                )
        );

        if (!tokens.isEmpty()) {
            VerificationToken lastToken = tokens.get(0);
            Timestamp now = Timestamp.from(Instant.now());
            if ((now.getTime() - lastToken.getLastVerificationRequest().getTime()) < RESEND_COOLDOWN_PERIOD) {
                logger.warn("Initiate password reset request throttled for user: {}", user.getUsername());
                throw new IllegalStateException("You can only request a password reset every 10 minutes.");
            }
        }

        String token = UUID.randomUUID().toString();
        String encryptedToken = passwordEncoder.encode(token);
        Timestamp expirationTime = Timestamp.from(Instant.now().plusSeconds(86400)); // 24 hours expiration
        Timestamp now = Timestamp.from(Instant.now());

        jdbcTemplate.update("INSERT INTO password_reset_token (user_id, token, expiration, is_used, last_verification_request) VALUES (?, ?, ?, ?, ?)",
                user.getUserId(), encryptedToken, expirationTime, false, now);

        String encryptedUrlToken = "";
        try {
            encryptedUrlToken = EncryptionUtil.encrypt(token);
        } catch (Exception e) {
            logger.error("Error encrypting token", e);
        }

        String resetLink = "http://localhost:5173/reset-password-form?token=" + encryptedUrlToken;
//        emailService.sendSimpleEmail(user.getEmail(), "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
        logger.info("Password reset email sent to: {}", user.getEmail());
    }

    public void resetPassword(String token, String newPassword) {
        logger.info("Attempting to reset password with token: {}", token);
        List<VerificationToken> tokens = jdbcTemplate.query(
                "SELECT * FROM password_reset_token WHERE is_used = false AND expiration > CURRENT_TIMESTAMP",
                (rs, rowNum) -> new VerificationToken(
                        rs.getInt("token_id"),
                        rs.getInt("user_id"),
                        rs.getString("token"),
                        rs.getTimestamp("expiration"),
                        rs.getBoolean("is_used"),
                        rs.getTimestamp("last_verification_request")
                )
        );

        for (VerificationToken storedToken : tokens) {
            if (passwordEncoder.matches(token, storedToken.getToken())) {
                AppUser user = appUserRepository.findById(storedToken.getUserId())
                        .orElseThrow(() -> {
                            logger.error("User not found with ID: {}", storedToken.getUserId());
                            return new EntityNotFoundException("User not found with ID: " + storedToken.getUserId());
                        });
                user.setPassword(passwordEncoder.encode(newPassword));
                appUserRepository.save(user);
                jdbcTemplate.update("UPDATE password_reset_token SET is_used = true WHERE token_id = ?", storedToken.getTokenId());
                logger.info("Password successfully reset for user: {}", user.getUsername());
                return;
            }
        }

        logger.error("Invalid or expired token: {}", token);
        throw new IllegalArgumentException("Invalid or expired token");
    }
}