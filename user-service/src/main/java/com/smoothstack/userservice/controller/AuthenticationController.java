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

package com.smoothstack.userservice.controller;

import com.smoothstack.userservice.model.*;
import com.smoothstack.userservice.service.AuthenticationService;
import com.smoothstack.userservice.service.PasswordResetService;
import com.smoothstack.userservice.service.VerificationTokenService;
import com.smoothstack.userservice.service.AppUserService;
import com.smoothstack.userservice.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final VerificationTokenService tokenService;
    private final AppUserService appUserService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) throws Exception {
        AuthenticationResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) throws Exception {
        VerificationToken verificationToken = tokenService.getVerificationToken(token);

        if (verificationToken == null || verificationToken.getExpiration().before(new java.sql.Timestamp(System.currentTimeMillis())) || verificationToken.isUsed()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        appUserService.confirmUser(verificationToken.getUserId());
        tokenService.markTokenAsUsed(verificationToken.getTokenId());

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resendConfirmation(@RequestParam("emailOrUsername") String emailOrUsername) {
        try {
            AppUser appUser = appUserService.findByEmailOrUsername(emailOrUsername);
            if (appUser == null) {
                return ResponseEntity.badRequest().body("User not found with the given email or username");
            }

            if (Boolean.TRUE.equals(appUser.getIsVerified())) {
                return ResponseEntity.badRequest().body("User is already verified");
            }

            tokenService.resendVerificationToken(appUser.getUserId());
            return ResponseEntity.ok("New verification link sent to your email");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(429).body(e.getMessage()); // 429 Too Many Requests
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while resending the verification link");
        }
    }

    @GetMapping("/verification-status")
    public ResponseEntity<Boolean> getVerificationStatus(@RequestParam("emailOrUsername") String emailOrUsername) {
        AppUser appUser = appUserService.findByEmailOrUsername(emailOrUsername);
        if (appUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Boolean.TRUE.equals(appUser.getIsVerified()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) throws Exception {
        String emailOrUsername = payload.get("emailOrUsername");
        if (emailOrUsername == null || emailOrUsername.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            passwordResetService.initiatePasswordReset(emailOrUsername);
            return ResponseEntity.ok("If your account exists, a recovery email will be sent.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(429).body(e.getMessage()); // 429 Too Many Requests
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while resending the password reset link");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Token and new password are required");
        }

        try {
            String decryptedToken = EncryptionUtil.decrypt(token);
            passwordResetService.resetPassword(decryptedToken, newPassword);
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while resetting the password.");
        }
    }
}