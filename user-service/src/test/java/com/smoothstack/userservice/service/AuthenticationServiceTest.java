package com.smoothstack.userservice.service;

import com.smoothstack.userservice.model.*;
import com.smoothstack.userservice.repository.AppUserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSuccessTest() throws Exception {
        // Mocking the repository response
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Mocking the password encoder
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Mocking the repository save method
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setUserId(123);
            return user;
        });

        // Mocking the verification token service
        when(verificationTokenService.generateToken()).thenReturn("token");

        // Mocking the JWT service
        when(jwtService.generateToken(any(AppUser.class), anyString(), anyInt())).thenReturn("jwtToken");

        // Call the register method
        AuthenticationResponse response = authenticationService.register(new RegisterRequest("newUser", "test@example.com", "password123!"));

        // Verify that the response is not null and contains the expected token
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("jwtToken", response.getToken());
        assertEquals(123, response.getUserId());

        // Verify that the repository save method is called with the correct user
        verify(repository).save(any(AppUser.class));

        // Verify that the verification token service is called to create a verification token
        verify(verificationTokenService).createVerificationToken(eq(123), eq("token"));

        // Verify that the email service is called to send a welcome email
        verify(emailService).sendWelcomeEmail(eq("test@example.com"), eq("newUser"), anyString());
    }


    @Test
    void registerUsernameExistsTest() {
        // Arrange
        RegisterRequest request = new RegisterRequest("existingUser", "test@example.com", "password123!");
        AppUser existingUser = AppUser.builder()
                .username(request.getUsername())
                .email("otheremail@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .userId(123)
                .build();

        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        EntityExistsException thrown = assertThrows(
                EntityExistsException.class,
                () -> authenticationService.register(request),
                "Expected register() to throw an exception, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Username already exists"));
    }


    @Test
    void authenticateSuccessTest() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user", "password123!");
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .isActive(true)
                .userId(123)
                .build();

        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtService.generateToken(any(), eq(user.getRole().name()), eq(user.getUserId()))).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }


    @Test
    void authenticateUserNotFoundTest() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("nonexistentUser", "password123!");
        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
    }
}
