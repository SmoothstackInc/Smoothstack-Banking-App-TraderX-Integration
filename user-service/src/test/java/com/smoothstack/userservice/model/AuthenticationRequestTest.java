package com.smoothstack.userservice.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationRequestTest {

    @Test
    public void testValidAuthenticationRequest() {
        // Create a valid AuthenticationRequest object
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("validUser")
                .password("ValidPassword123!")
                .build();

        // Validate the object using Bean Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);

        // Expect no validation errors
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmptyUsername() {
        // Create an AuthenticationRequest with an empty username
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("")
                .password("ValidPassword123!")
                .build();

        // Validate the object using Bean Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);

        // Expect a validation error for the empty username
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyPassword() {
        // Create an AuthenticationRequest with an empty password
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("validUser")
                .password("")
                .build();

        // Validate the object using Bean Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);

        // Expect a validation error for the empty password
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testToStringMethod() {
        // Create an AuthenticationRequest object
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("validUser")
                .password("ValidPassword123!")
                .build();

        // Call the toString() method
        String toStringResult = request.toString();

        // Define the expected format of the toString() result
        String expectedFormat = "AuthenticationRequest(username=validUser, password=ValidPassword123!)";

        // Assert that the toString() method result matches the expected format
        assertEquals(expectedFormat, toStringResult);
    }

    @Test
    public void testHashCodeConsistency() {
        AuthenticationRequest request1 = new AuthenticationRequest("validUser", "ValidPassword123!");
        AuthenticationRequest request2 = new AuthenticationRequest("validUser", "ValidPassword123!");

        assertEquals(request1.hashCode(), request2.hashCode(), "Hash codes should be the same for equal objects");
    }

}
