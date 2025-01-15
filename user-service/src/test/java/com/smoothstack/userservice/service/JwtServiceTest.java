package com.smoothstack.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private String getSecret() {
        String secretName = "jwt-key";
        Region region = Region.of("us-east-1");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            return getSecretValueResponse.secretString();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving secret: " + e.getMessage());
        }
    }

    private String secretKey = String.valueOf(getSecret());

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        // Set the secretKey in JwtService
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    }

    @Test
    void generateTokenTest() {
        // Arrange
        String role = "CUSTOMER";
        Integer userId = 12345; // Example userId
        UserDetails userDetails = new User("testUser", "password123", new ArrayList<>());

        // Act
        String token = jwtService.generateToken(userDetails, role, userId);

        // Assert
        assertNotNull(token);
    }

    @Test
    void extractAppUserNameTest() {
        // Arrange
        String expectedUsername = "testUser";
        String role = "CUSTOMER";
        Integer userId = 12345; // Example userId
        UserDetails userDetails = new User(expectedUsername, "password123", new ArrayList<>());
        String token = jwtService.generateToken(userDetails, role, userId);

        // Act
        String actualUsername = jwtService.extractAppUserName(token);

        // Assert
        assertNotNull(actualUsername);
        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    void isTokenValidTest_ValidToken() {
        // Arrange
        String role = "CUSTOMER";
        Integer userId = 12345; // Example userId
        UserDetails userDetails = new User("testUser", "password123", new ArrayList<>());
        String token = jwtService.generateToken(userDetails, role, userId);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValidTest_InvalidToken() {
        // Arrange
        String role = "CUSTOMER";
        Integer userId = 12345; // Example userId
        UserDetails userDetails = new User("testUser", "password123", new ArrayList<>());
        UserDetails wrongUserDetails = new User("wrongUser", "password123", new ArrayList<>());
        String token = jwtService.generateToken(userDetails, role, userId);

        // Act
        boolean isValid = jwtService.isTokenValid(token, wrongUserDetails);

        // Assert
        assertFalse(isValid);
    }

}
