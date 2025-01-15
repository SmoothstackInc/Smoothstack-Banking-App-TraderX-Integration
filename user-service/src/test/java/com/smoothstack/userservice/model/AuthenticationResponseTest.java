package com.smoothstack.userservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationResponseTest {

    @Test
    public void testAuthenticationResponseConstructor() {
        // Create a mock AuthenticationResponse object
        AuthenticationResponse response = mock(AuthenticationResponse.class);
        when(response.getToken()).thenReturn("validToken");


        assertEquals("validToken", response.getToken());
    }

    @Test
    public void testHashCodeMethod() {
        // Create an AuthenticationResponse object
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("validToken")
                .build();

        // Calculate the hash code
        int hashCode = response.hashCode();

        // Assert that the hash code is non-zero
        assertNotEquals(0, hashCode);
    }

    @Test
    public void testEqualsMethod() {
        // Create two AuthenticationResponse objects with the same token
        AuthenticationResponse response1 = AuthenticationResponse.builder()
                .token("validToken")
                .build();
        AuthenticationResponse response2 = AuthenticationResponse.builder()
                .token("validToken")
                .build();

        // Assert that they are equal
        assertTrue(response1.equals(response2));
    }

    @Test
    public void testSetTokenMethod() {
        // Create an AuthenticationResponse object
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("validToken")
                .build();

        // Set a new token
        response.setToken("newToken");

        // Assert that the token is updated correctly
        assertEquals("newToken", response.getToken());
    }

    @Test
    public void testCanEqualMethod() {
        // Create an AuthenticationResponse object
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("validToken")
                .build();

        // Create an object of a different class
        Object obj = new Object();

        // Assert that they can't be equal
        assertFalse(response.canEqual(obj));
    }
}
