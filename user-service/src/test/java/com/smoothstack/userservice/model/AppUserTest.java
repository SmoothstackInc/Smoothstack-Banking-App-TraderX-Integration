package com.smoothstack.userservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AppUserTest {

    private AppUser appUser;

    @BeforeEach
    public void setUp() {
        // Initialize an AppUser object before each test
        appUser = new AppUser();
    }

    @Test
    void setAndGetSecretAnswer() {
        String secretAnswer = "MySecretAnswer";
        appUser.setSecretAnswer(secretAnswer);
        assertEquals(secretAnswer, appUser.getSecretAnswer());
    }

    @Test
    void setAndGetSecretQuestion() {
        String secretQuestion = "MySecretQuestion";
        appUser.setSecretQuestion(secretQuestion);
        assertEquals(secretQuestion, appUser.getSecretQuestion());
    }

    @Test
    void setAndGetFirstName() {
        String firstName = "Anton";
        appUser.setFirstName(firstName);
        assertEquals(firstName, appUser.getFirstName());
    }

    @Test
    void setAndGetLastName() {
        String lastName = "Miles";
        appUser.setLastName(lastName);
        assertEquals(lastName, appUser.getLastName());
    }

    @Test
    void setAndGetDateOfBirth() {
        Date dateOfBirth = new Date();
        appUser.setDateOfBirth(dateOfBirth);
        assertEquals(dateOfBirth, appUser.getDateOfBirth());
    }

    @Test
    void setAndGetDateModified() {
        Date dateModified = new Date();
        appUser.setDateModified(dateModified);
        assertEquals(dateModified, appUser.getDateModified());
    }

    @Test
    void setAndGetDateCreated() {
        Date dateCreated = new Date();
        appUser.setDateCreated(dateCreated);
        assertEquals(dateCreated, appUser.getDateCreated());
    }

    @Test
    void setAndGetIsVerified() {
        appUser.setIsVerified(true);
        assertTrue(appUser.getIsVerified());

        appUser.setIsVerified(false);
        assertFalse(appUser.getIsVerified());
    }

    @Test
    void testIsEnabled() {
        assertTrue(appUser.isEnabled());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(appUser.isCredentialsNonExpired());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(appUser.isAccountNonExpired());
    }

    @Test
    void setAndGetAddress() {
        String address = "123 Main St";
        appUser.setAddress(address);
        assertEquals(address, appUser.getAddress());
    }

    @Test
    void setAndGetPhoneNumber() {
        Long phoneNumber = 1234567890L;
        appUser.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, appUser.getPhoneNumber());
    }

    @Test
    void setAndGetIsActive() {
        appUser.setIsActive(true);
        assertTrue(appUser.getIsActive());

        appUser.setIsActive(false);
        assertFalse(appUser.getIsActive());
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getters and setters of the AppUser class

        // Set values using setters
        appUser.setUserId(1);
        appUser.setUsername("testUser");
        appUser.setPassword("Password123!");
        appUser.setEmail("test@example.com");
        appUser.setRole(Role.CUSTOMER); // Set the role to CUSTOMER
        appUser.setFailedLoginAttempts(2);

        // Check if values can be retrieved using getters
        assertEquals(1, appUser.getUserId());
        assertEquals("testUser", appUser.getUsername());
        assertEquals("Password123!", appUser.getPassword());
        assertEquals("test@example.com", appUser.getEmail());
        assertEquals(Role.CUSTOMER, appUser.getRole());
        assertEquals(2, appUser.getFailedLoginAttempts());
    }

    @Test
    public void testResetFailedLoginAttempts() {
        // Test the resetFailedLoginAttempts method

        // Set some failed login attempts and lock the account
        appUser.setFailedLoginAttempts(3);
        appUser.setLockTime(new Date());

        // Call the resetFailedLoginAttempts method
        appUser.resetFailedLoginAttempts();

        // Check if failed login attempts are reset and lock time is null
        assertEquals(0, appUser.getFailedLoginAttempts());
        assertNull(appUser.getLockTime());
    }

    @Test
    public void testIncrementFailedLoginAttempts() {
        // Test the incrementFailedLoginAttempts method

        // Set some failed login attempts
        appUser.setFailedLoginAttempts(2);

        // Call the incrementFailedLoginAttempts method
        appUser.incrementFailedLoginAttempts();

        // Check if failed login attempts are incremented
        assertEquals(3, appUser.getFailedLoginAttempts());

        // Check if lock time is set after reaching the maximum failed attempts
        appUser.incrementFailedLoginAttempts();
        appUser.incrementFailedLoginAttempts();
        assertNotNull(appUser.getLockTime());
    }

    @Test
    public void testIsAccountNonLockedWhenLockTimeIsNull() {
        // Test the isAccountNonLocked method when lockTime is null

        appUser.setLockTime(null);

        // Call isAccountNonLocked
        assertTrue(appUser.isAccountNonLocked());
    }

    @Test
    public void testIsAccountNonLockedWhenLockTimeIsExpired() {
        // Test the isAccountNonLocked method when lockTime is expired

        // Set lockTime to a date in the past
        appUser.setLockTime(new Date(System.currentTimeMillis() - 2 * 3600 * 1000)); // 2 hours ago

        // Call isAccountNonLocked
        assertTrue(appUser.isAccountNonLocked());
    }

    @Test
    public void testIsAccountNonLockedWhenLockTimeIsNotExpired() {
        // Test the isAccountNonLocked method when lockTime is not expired

        // Set lockTime to a date in the future
        appUser.setLockTime(new Date(System.currentTimeMillis() + 2 * 3600 * 1000)); // 2 hours in the future

        // Call isAccountNonLocked
        assertFalse(appUser.isAccountNonLocked());
    }

    @Test
    public void testCanEqualWithSameObject() {
        // Create two AppUser objects with the same attributes
        AppUser user1 = new AppUser();
        user1.setUserId(1);

        // Both objects should be equal
        assertTrue(appUser.canEqual(user1));
    }

    @Test
    void testHashCodeAndEquals() {
        AppUser user1 = new AppUser();
        user1.setUserId(1);
        AppUser user2 = new AppUser();
        user2.setUserId(1);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertEquals(user1, user2);

        user2.setUserId(2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user2);
    }

    @Test
    void testOnCreateAndUpdate() {
        appUser.onCreate(); // Assuming onCreate sets the dateCreated field
        assertNotNull(appUser.getDateCreated());

        appUser.onUpdate(); // Assuming onUpdate sets the dateModified field
        assertNotNull(appUser.getDateModified());
    }


}
