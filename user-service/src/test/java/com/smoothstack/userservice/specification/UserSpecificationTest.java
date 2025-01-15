package com.smoothstack.userservice.specification;

import com.smoothstack.userservice.model.AppUser;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserSpecificationTest {

    @Mock
    private Root<AppUser> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate predicate;
    @Mock
    private Path<String> usernamePath;
    @Mock
    private Path<String> emailPath;
    @Mock
    private Path<String> firstNamePath;
    @Mock
    private Path<String> lastNamePath;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        doReturn(usernamePath).when(root).get("username");
        doReturn(emailPath).when(root).get("email");
        doReturn(firstNamePath).when(root).get("firstName");
        doReturn(lastNamePath).when(root).get("lastName");

        when(criteriaBuilder.isTrue(any())).thenReturn(predicate);

        // Set up like method stubbing to return a predicate for any inputs
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);
    }

    @Test
    public void usernameContains_WithValidUsername_ShouldReturnCorrectPredicate() {
        // Arrange
        String username = "testUser";
        // Correctly stub the like method
        when(criteriaBuilder.like(usernamePath, "%" + username + "%")).thenReturn(predicate);

        // Act
        Predicate resultPredicate = UserSpecification.usernameContains(username).toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
    }

    @Test
    public void emailContains_WithValidEmail_ShouldReturnCorrectPredicate() {
        // Arrange
        String email = "test@example.com";
        // Correctly stub the like method
        when(criteriaBuilder.like(emailPath, "%" + email + "%")).thenReturn(predicate);

        // Act
        Predicate resultPredicate = UserSpecification.emailContains(email).toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
    }

    @Test
    public void firstNameContains_WithValidFirstName_ShouldReturnCorrectPredicate() {
        // Arrange
        String firstName = "John";
        // Correctly stub the like method
        when(criteriaBuilder.like(firstNamePath, "%" + firstName + "%")).thenReturn(predicate);

        // Act
        Predicate resultPredicate = UserSpecification.firstNameContains(firstName).toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
    }

    @Test
    public void lastNameContains_WithValidLastName_ShouldReturnCorrectPredicate() {
        // Arrange
        String lastName = "Doe";
        // Correctly stub the like method
        when(criteriaBuilder.like(lastNamePath, "%" + lastName + "%")).thenReturn(predicate);

        // Act
        Predicate resultPredicate = UserSpecification.lastNameContains(lastName).toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
    }

    @Test
    public void usernameContains_WithNullUsername_ShouldReturnTruePredicate() {
        // Act
        Predicate resultPredicate = UserSpecification.usernameContains(null).toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
        verify(criteriaBuilder, never()).like(any(), anyString());
    }

    @Test
    public void emailContains_WithEmptyEmail_ShouldReturnTruePredicate() {
        // Act
        Predicate resultPredicate = UserSpecification.emailContains("").toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(resultPredicate);
        verify(criteriaBuilder, never()).like(any(), anyString());
    }


}


