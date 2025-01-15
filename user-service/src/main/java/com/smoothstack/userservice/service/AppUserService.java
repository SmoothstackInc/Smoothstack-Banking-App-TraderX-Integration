/*
 * TraderX - A trading automation software.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.userservice.service;

import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.dto.UserResponseDTO;
import com.smoothstack.userservice.mapper.AppUserMapper;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.repository.AppUserRepository;
import com.smoothstack.userservice.specification.UserSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AppUserService {

    private static final Logger logger = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AppUserDTO> getAllUsersDto() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public Page<AppUserDTO> getAllUsersWithPagination(Pageable pageable) {
        return appUserRepository.findAll(pageable)
                .map(appUserMapper::userToUserDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == principal.userId")
    public AppUserDTO getUserDtoById(Integer userId) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return appUserMapper.userToUserDTO(appUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUserById(Integer userId) {
        boolean userExists = appUserRepository.existsById(userId);
        if (!userExists) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        appUserRepository.deleteById(userId);
        return "User with ID: " + userId + " has been successfully deleted.";
    }

    public AppUserDTO createUser(AppUserDTO userDTO) {
        // Check if the username already exists
        if (appUserRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }

        AppUser newUser = appUserMapper.userDTOToUser(userDTO);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Ensure password is encoded
        AppUser createdUser = appUserRepository.save(newUser);
        return appUserMapper.userToUserDTO(createdUser);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == principal.userId")
    public UserResponseDTO updateUser(Integer userId, AppUserDTO userDTO) {
        logger.info("Updating user with ID: {}", userId);
        logger.info("Received user DTO: {}", userDTO);

        AppUser existingUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Check if the username is being updated to a new value that already exists
        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(existingUser.getUsername()) &&
                appUserRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }

        // Check if the email is being updated to a new value that already exists
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail()) &&
                appUserRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        // Use MapStruct to update the existing user with non-null properties from the DTO
        appUserMapper.updateUserFromDtoIgnoringNull(userDTO, existingUser);

        // Encrypt and update password if it's being updated
        if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
            logger.info("Encrypting new password for user ID: {}", userId);
            existingUser.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        } else {
            // Log that the password is not being changed
            logger.info("Password is not being changed for user ID: {}", userId);
        }

        AppUser updatedUser = appUserRepository.save(existingUser);
        logger.info("Updated user: {}", updatedUser);
        return appUserMapper.userToUserResponseDTO(updatedUser);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<AppUserDTO> searchUsers(String username, String email, String firstName, String lastName, Pageable pageable) {
        Specification<AppUser> spec = Specification.where(UserSpecification.usernameContains(username))
                .and(UserSpecification.emailContains(email))
                .and(UserSpecification.firstNameContains(firstName))
                .and(UserSpecification.lastNameContains(lastName));

        // Directly return the Page<AppUserDTO> by mapping the Page<AppUser> to DTOs
        return appUserRepository.findAll(spec, pageable).map(appUserMapper::userToUserDTO);
    }

    public AppUser findByEmailOrUsername(String emailOrUsername) {
        return appUserRepository.findByEmail(emailOrUsername)
                .or(() -> appUserRepository.findByUsername(emailOrUsername))
                .orElseThrow(() -> new EntityNotFoundException("User not found with email or username: " + emailOrUsername));
    }

    public void confirmUser(int userId) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        appUser.setIsVerified(true);
        appUserRepository.save(appUser);
    }
}
