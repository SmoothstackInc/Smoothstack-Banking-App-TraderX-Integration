package com.smoothstack.userservice.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AppUserDTOTest {

    @Test
    void testAppUserDTO() {
        AppUserDTO user = new AppUserDTO();

        // Set values
        user.setUserId(1);
        user.setUsername("TestUser");
        user.setPassword("Password123!");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsVerified(true);
        user.setIsActive(true);
        user.setDateCreated(new Date());
        user.setDateModified(new Date());
        user.setDateOfBirth(new Date());
        user.setPhoneNumber(1234567890L);
        user.setAddress("123 Test Street");
        user.setSecretQuestion("Test Question?");
        user.setSecretAnswer("Test Answer");
        user.setRole("CUSTOMER");
        user.setFailedLoginAttempts(0);
        user.setLockTime(new Date());

        // Verify values
        assertEquals(1, user.getUserId());
        assertEquals("TestUser", user.getUsername());
        assertEquals("Password123!", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertTrue(user.getIsVerified());
        assertTrue(user.getIsActive());
        assertNotNull(user.getDateCreated());
        assertNotNull(user.getDateModified());
        assertNotNull(user.getDateOfBirth());
        assertEquals(1234567890, user.getPhoneNumber());
        assertEquals("123 Test Street", user.getAddress());
        assertEquals("Test Question?", user.getSecretQuestion());
        assertEquals("Test Answer", user.getSecretAnswer());
        assertEquals("CUSTOMER", user.getRole());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getLockTime());
    }

    @Test
    void testEqualityAndHashCode() {
        AppUserDTO user1 = new AppUserDTO();
        user1.setUserId(1);
        user1.setUsername("TestUser");

        AppUserDTO user2 = new AppUserDTO();
        user2.setUserId(1);
        user2.setUsername("TestUser");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testInequality() {
        AppUserDTO user1 = new AppUserDTO();
        user1.setUserId(1);
        user1.setUsername("TestUser");

        AppUserDTO user2 = new AppUserDTO();
        user2.setUserId(2);
        user2.setUsername("DifferentUser");

        assertNotEquals(user1, user2);
    }

    @Test
    void testToString() {
        AppUserDTO user = new AppUserDTO();
        user.setUsername("TestUser");

        String toStringResult = user.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("TestUser"));
    }

    @Test
    void testNullValues() {
        AppUserDTO user = new AppUserDTO();
        assertNull(user.getUsername());
    }
}
