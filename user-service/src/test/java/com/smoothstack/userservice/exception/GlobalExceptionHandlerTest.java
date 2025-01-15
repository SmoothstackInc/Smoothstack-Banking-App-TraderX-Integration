package com.smoothstack.userservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {GlobalExceptionHandler.class})
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;


    @Test
    void handleEntityNotFoundException_ShouldReturnNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<String> response = globalExceptionHandler.handleEntityNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody());
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnBadRequest() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity violation");
        ResponseEntity<String> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Database error:"));
    }



    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequest() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        BindException bindException = new BindException(new Object(), "target");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindException);

        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

        ResponseEntity<String> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Malformed JSON"));
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<String> response = globalExceptionHandler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Access denied"));
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("General error");

        ResponseEntity<String> response = globalExceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("General error"));
    }

    @Test
    void handleAuthenticationException_ShouldReturnUnauthorized() {
        AuthenticationException ex = new AuthenticationException("Authentication failed") {};

        ResponseEntity<String> response = globalExceptionHandler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Authentication failed"));
    }

    @Test
    void handleUsernameNotFoundException_ShouldReturnNotFound() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");

        ResponseEntity<String> response = globalExceptionHandler.handleUsernameNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("User not found"));
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<String> response = globalExceptionHandler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Bad credentials"));
    }

    @Test
    void handleLockedException_ShouldReturnUnauthorized() {
        LockedException ex = new LockedException("Account locked");

        ResponseEntity<String> response = globalExceptionHandler.handleLockedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Account locked"));
    }
}
