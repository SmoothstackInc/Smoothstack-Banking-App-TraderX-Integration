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

package com.smoothstack.userservice.mapper;

import com.smoothstack.userservice.dto.UserResponseDTO;
import com.smoothstack.userservice.model.AppUser;
import com.smoothstack.userservice.dto.AppUserDTO;
import org.mapstruct.*;

// The componentModel = "spring" attribute makes the generated mapper a Spring bean,
// allowing it to be injected into other Spring components.
@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDTO userToUserDTO(AppUser appUser);
    AppUser userDTOToUser(AppUserDTO userDTO);

    @Mapping(target = "userId", ignore = true) // Ignoring the ID field
    void updateUserFromDto(AppUserDTO dto, @MappingTarget AppUser user);  // updates an existing user with params

    UserResponseDTO userToUserResponseDTO(AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDtoIgnoringNull(AppUserDTO dto, @MappingTarget AppUser user);
}


