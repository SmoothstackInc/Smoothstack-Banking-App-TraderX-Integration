package com.smoothstack.userservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Create a validator factory and get the validator (initialize it before each test)
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidRegisterRequest() {
        // Create a valid RegisterRequest object
        RegisterRequest request = RegisterRequest.builder()
                .username("validUser")
                .email("valid@example.com")
                .password("ValidPassword123!")
                .build();

        // Assert that the object is not null
        assertNotNull(request);
    }


    @Test
    public void testInvalidEmail() {
        // Create a RegisterRequest with an invalid email
        RegisterRequest request = RegisterRequest.builder()
                .username("validUser")
                .email("invalid-email")
                .password("ValidPassword123!")
                .build();

        // Validate the object using Bean Validation
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Expect a validation error for the invalid email
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidPassword() {
        // Create a RegisterRequest with a valid password
        RegisterRequest request = RegisterRequest.builder()
                .username("validUser")
                .email("valid@example.com")
                .password("ValidPassword123!")
                .build();

        // Validate the object using Bean Validation
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Expect no validation errors for the valid password
        assertTrue(violations.isEmpty());
    }
    @Test
    public void testHashCodeConsistency() {
        RegisterRequest request1 = RegisterRequest.builder()
                .username("user")
                .email("user@example.com")
                .password("Password123!")
                .build();

        RegisterRequest request2 = RegisterRequest.builder()
                .username("user")
                .email("user@example.com")
                .password("Password123!")
                .build();

        assertEquals(request1, request2, "Objects should be equal");
        assertEquals(request1.hashCode(), request2.hashCode(), "Hash codes should be the same for equal objects");
    }

    @Test
    public void testToStringContainsUsername() {
        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .email("user@example.com")
                .password("Password123!")
                .build();

        String toStringResult = request.toString();
        assertNotNull(toStringResult, "toString should not return null");
        assertTrue(toStringResult.contains("user"), "toString should contain the username");
    }

    @Test
    public void testToStringContainsEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .email("user@example.com")
                .password("Password123!")
                .build();

        String toStringResult = request.toString();
        assertTrue(toStringResult.contains("user@example.com"), "toString should contain the email");
    }
}
