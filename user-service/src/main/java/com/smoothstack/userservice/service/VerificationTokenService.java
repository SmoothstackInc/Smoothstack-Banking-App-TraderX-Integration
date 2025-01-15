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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class VerificationTokenService {

    private static final long RESEND_COOLDOWN_PERIOD = Duration.of(10, ChronoUnit.MINUTES).toMillis();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;



    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void createVerificationToken(int userId, String token) throws Exception {
        String encryptedToken = passwordEncoder.encode(token);
        Timestamp expirationTime = Timestamp.from(Instant.now().plusSeconds(86400)); // 24 hours expiration
        Timestamp now = Timestamp.from(Instant.now());
        jdbcTemplate.update("INSERT INTO verification_token (user_id, token, expiration, last_verification_request) VALUES (?, ?, ?, ?)",
                userId, encryptedToken, expirationTime, now);
    }

    public VerificationToken getVerificationToken(String token) throws Exception {
        String decryptedToken = EncryptionUtil.decrypt(token);
        List<VerificationToken> tokens = jdbcTemplate.query(
                "SELECT * FROM verification_token WHERE is_used = false AND expiration > CURRENT_TIMESTAMP",
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
            if (passwordEncoder.matches(decryptedToken, storedToken.getToken())) {
                return storedToken;
            }
        }

        return null;
    }

    public void markTokenAsUsed(int tokenId) {
        jdbcTemplate.update("UPDATE verification_token SET is_used = true WHERE token_id = ?", tokenId);
    }

    public void resendVerificationToken(int userId) throws Exception {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<VerificationToken> tokens = jdbcTemplate.query(
                "SELECT * FROM verification_token WHERE user_id = ? ORDER BY last_verification_request DESC LIMIT 1",
                new Object[]{userId},
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
                throw new IllegalStateException("You can only request a new verification email every 10 minutes.");
            }
        }

        String newToken = generateToken();
        String encryptedToken = passwordEncoder.encode(newToken);
        Timestamp expirationTime = Timestamp.from(Instant.now().plusSeconds(86400)); // 24 hours expiration
        Timestamp now = Timestamp.from(Instant.now());
        jdbcTemplate.update("INSERT INTO verification_token (user_id, token, expiration, last_verification_request) VALUES (?, ?, ?, ?)",
                userId, encryptedToken, expirationTime, now);

        // Encrypt the token before adding it to the URL
        String encryptedNewToken = EncryptionUtil.encrypt(newToken);

        String verificationLink = "http://localhost:8085/api/v1/auth/confirm?token=" + encryptedNewToken;

    }
}