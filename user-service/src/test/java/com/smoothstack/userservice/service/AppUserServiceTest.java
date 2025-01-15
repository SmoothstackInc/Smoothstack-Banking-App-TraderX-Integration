package com.smoothstack.userservice.service;

import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.dto.UserResponseDTO;
import com.smoothstack.userservice.mapper.AppUserMapper;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.model.Role;
import com.smoothstack.userservice.repository.AppUserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Base64Util.encode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.ArgumentMatchers.any;



public class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void createUserTest_Success() {
        // Arrange
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setUsername("validUsername");
        userDTO.setPassword("ValidPass123!");
        userDTO.setEmail("valid@test.com");

        AppUser appUser = AppUser.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .isVerified(false)
                .isActive(true)
                .role(Role.CUSTOMER)
                .failedLoginAttempts(0)
                .build();
        when(appUserRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(appUserMapper.userDTOToUser(userDTO)).thenReturn(appUser);
        when(passwordEncoder.encode(anyString())).thenReturn("VmFsaWRQYXNzMTIzIQ=="); // Mocked encoded password
        System.out.println(appUser);
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.userToUserDTO(appUser)).thenReturn(userDTO);

        // Act
        AppUserDTO result = appUserService.createUser(userDTO);

        // Assert
        assertNotNull(result);
        verify(appUserRepository).findByUsername(userDTO.getUsername());
        System.out.println(encode("ValidPass123!"));
        verify(passwordEncoder).encode(userDTO.getPassword()); // Verify password encoding
        verify(appUserRepository).save(appUser);
    }


    @Test
    public void createUserTest_UserExists() {
        // Arrange
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setUsername("existingUser");
        userDTO.setPassword("Password123!");
        userDTO.setEmail("existing@test.com");
        when(appUserRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(new AppUser()));

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> appUserService.createUser(userDTO));
    }


    @Test
    public void getUserDtoByIdTest_Success() {
        // Arrange
        Integer userId = 1;
        AppUser appUser = new AppUser(); // populate with test data
        AppUserDTO appUserDTO = new AppUserDTO(); // populate with test data
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(appUser));
        when(appUserMapper.userToUserDTO(appUser)).thenReturn(appUserDTO);

        // Act
        AppUserDTO result = appUserService.getUserDtoById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(appUserDTO, result);
        verify(appUserRepository).findById(userId);
        verify(appUserMapper).userToUserDTO(appUser);
    }

    @Test
    public void getUserDtoByIdTest_NotFound() {
        // Arrange
        Integer userId = 1;
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> appUserService.getUserDtoById(userId));
    }



    @Test
    public void deleteUserByIdTest_Success() {
        // Arrange
        Integer userId = 1;
        when(appUserRepository.existsById(userId)).thenReturn(true);

        // Act
        String result = appUserService.deleteUserById(userId);

        // Assert
        assertEquals("User with ID: " + userId + " has been successfully deleted.", result);
        verify(appUserRepository).existsById(userId);
        verify(appUserRepository).deleteById(userId);
    }

    @Test
    public void deleteUserByIdTest_NotFound() {
        // Arrange
        Integer userId = 1;
        when(appUserRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> appUserService.deleteUserById(userId));
    }

    @Test
    public void getAllUsersDtoTest() {
        // Arrange
        AppUser appUser = new AppUser(); // populate with test data
        AppUserDTO appUserDTO = new AppUserDTO(); // populate with test data
        when(appUserRepository.findAll()).thenReturn(Arrays.asList(appUser));
        when(appUserMapper.userToUserDTO(appUser)).thenReturn(appUserDTO);

        // Act
        List<AppUserDTO> result = appUserService.getAllUsersDto();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(appUserDTO, result.get(0));
        verify(appUserRepository).findAll();
        verify(appUserMapper).userToUserDTO(appUser);
    }

    @Test
    public void updateUserTest_Success() {
        // Arrange
        Integer userId = 1;
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setUsername("updatedUser");
        userDTO.setPassword("UpdatedPass123!");

        AppUser existingUser = new AppUser();
        existingUser.setUserId(userId);
        existingUser.setUsername("originalUser");

        UserResponseDTO userResponseDTO = new UserResponseDTO(); // Create a UserResponseDTO object
        // Initialize properties of userResponseDTO as needed

        when(appUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(appUserRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(appUserRepository.save(any(AppUser.class))).thenReturn(existingUser);
        when(appUserMapper.userToUserResponseDTO(any(AppUser.class))).thenReturn(userResponseDTO); // Corrected to use userToUserResponseDTO

        // Act
        UserResponseDTO result = appUserService.updateUser(userId, userDTO); // Change expected return type to UserResponseDTO

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO, result); // Update your assertions accordingly
        verify(appUserRepository).findById(userId);
        verify(appUserRepository).save(any(AppUser.class));
    }

}