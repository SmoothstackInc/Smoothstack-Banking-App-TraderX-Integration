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

import com.smoothstack.userservice.model.*;
import com.smoothstack.userservice.repository.AppUserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        var user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .role(Role.CUSTOMER)
                .build();

        // Save the user and use the returned instance which includes the generated userId
        AppUser savedUser = repository.save(user);

        // Generate JWT token with userDetails, role, and userId
        var jwtToken = jwtService.generateToken(savedUser, savedUser.getRole().name(), savedUser.getUserId());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String username = request.getUsername();
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new LockedException("Account is inactive. Access denied.");
        }

        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked. Please try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            user.resetFailedLoginAttempts(); // Resetting the failed attempts on successful login
        } catch (BadCredentialsException e) {
            user.incrementFailedLoginAttempts();
            repository.save(user); // Persist the updated user data to the database
            throw new BadCredentialsException("Invalid username or password");
        }

        // Generate JWT token after successful authentication
        var jwtToken = jwtService.generateToken(user, user.getRole().name(), user.getUserId());

        // Return the authentication response with the JWT token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}