package com.smoothstack.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.dto.UserResponseDTO;
import com.smoothstack.userservice.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUserDTO appUserDTO;

    @BeforeEach
    public void setup() {
        appUserDTO = new AppUserDTO();
        // Initialize appUserDTO properties as needed
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteUserById() throws Exception {
        String responseMessage = "User deleted successfully";

        given(appUserService.deleteUserById(1)).willReturn(responseMessage);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "testUser", authorities = {"ADMIN", "USER"})
    public void getUserByIdWithAdminRole() throws Exception {
        given(appUserService.getUserDtoById(1)).willReturn(appUserDTO);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(appUserDTO)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testCreateUser() throws Exception {
        AppUserDTO newUser = new AppUserDTO();
        newUser.setUsername("newUser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("Password123!"); // Add a valid password

        given(appUserService.createUser(any(AppUserDTO.class))).willReturn(newUser);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newUser)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetAllUsers() throws Exception {
        List<AppUserDTO> userList = new ArrayList<>(); // Create a list of user DTOs with some sample data
        userList.add(new AppUserDTO());
        userList.add(new AppUserDTO());

        // Create a PageImpl object to simulate the Page<AppUserDTO> returned by the service
        Page<AppUserDTO> userPage = new PageImpl<>(userList);

        // Mock the service method to return the Page object
        given(appUserService.getAllUsersWithPagination(any(Pageable.class))).willReturn(userPage);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                // Use objectMapper.writeValueAsString on the Page object directly
                .andExpect(content().json(objectMapper.writeValueAsString(userPage)));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = {"ADMIN", "USER"})
    public void testUpdateUser() throws Exception {
        Integer userId = 1;
        AppUserDTO updatedUserDTO = new AppUserDTO();
        updatedUserDTO.setUsername("updatedUser");
        updatedUserDTO.setEmail("updated@example.com");
        updatedUserDTO.setPassword("UpdatedPassword123!"); // Valid password

        // Create and initialize a UserResponseDTO object
        UserResponseDTO updatedUserResponseDTO = new UserResponseDTO();
        // Initialize properties of updatedUserResponseDTO as needed
        // For example: updatedUserResponseDTO.setSomeProperty(...);

        // Stub the service call
        given(appUserService.updateUser(userId, updatedUserDTO)).willReturn(updatedUserResponseDTO);

        // Perform the test
        mockMvc.perform(patch("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedUserResponseDTO)));
    }
}
