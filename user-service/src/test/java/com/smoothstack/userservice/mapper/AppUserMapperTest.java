package com.smoothstack.userservice.mapper;

import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.model.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AppUserMapperTest {

    private final AppUserMapper mapper = Mappers.getMapper(AppUserMapper.class);

    @Test
    void shouldMapUserToUserDTO() {
        // Arrange
        AppUser user = AppUser.builder()
                .username("newUser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();

        // Act
        AppUserDTO userDTO = mapper.userToUserDTO(user);

        // Assert
        assertNotNull(userDTO);
        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getEmail(), userDTO.getEmail());
    }

    @Test
    void shouldMapUserDTOToUser() {
        // Arrange
        AppUserDTO userDTO = new AppUserDTO();
        userDTO.setUsername("newUser");
        userDTO.setEmail("test@example.com");

        // Act
        AppUser user = mapper.userDTOToUser(userDTO);

        // Assert
        assertNotNull(user);
        assertEquals(userDTO.getUsername(), user.getUsername());
        assertEquals(userDTO.getEmail(), user.getEmail());
    }
}
