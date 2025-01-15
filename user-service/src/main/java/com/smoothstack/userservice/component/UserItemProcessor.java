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

package com.smoothstack.userservice.component;

import com.smoothstack.userservice.dto.AppUserDTO;
import com.smoothstack.userservice.exception.SkippedUserException;
import com.smoothstack.userservice.mapper.AppUserMapper;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.repository.AppUserRepository;
import com.smoothstack.userservice.service.DuplicateCheckService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

@Component
public class UserItemProcessor implements ItemProcessor<AppUserDTO, AppUser> {

    private final DuplicateCheckService duplicateCheckService;
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final Validator validator;

    public UserItemProcessor(DuplicateCheckService duplicateCheckService,
                             AppUserRepository appUserRepository,
                             AppUserMapper appUserMapper,
                             Validator validator) {
        this.duplicateCheckService = duplicateCheckService;
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.validator = validator;
    }

    @Override
    public AppUser process(AppUserDTO dto) throws SkippedUserException {
        System.out.println("Processing user: " + dto.getUsername() + " on thread: " + Thread.currentThread().getName());

        Set<ConstraintViolation<AppUserDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (duplicateCheckService.checkDuplicate(dto.getUsername())) {
            String decision = duplicateCheckService.getDecision(dto.getUsername());
            switch (decision) {
                case "SKIP":
                    throw new SkippedUserException("Skipping user due to duplicate decision.");
                case "OVERWRITE":
                    return handleOverwrite(dto);
                default:
                    System.out.println("No decision yet for user: " + dto.getUsername() + ", treating as pending.");
                    throw new SkippedUserException("Duplicates detected.");
            }
        }
        return appUserMapper.userDTOToUser(dto);
    }

    private AppUser handleOverwrite(AppUserDTO dto) {
        AppUser existingUser = appUserRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + dto.getUsername()));
        appUserMapper.updateUserFromDto(dto, existingUser);
        appUserRepository.save(existingUser);
        return existingUser;
    }
}
