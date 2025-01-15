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

package com.smoothstack.userservice.dto;

import com.smoothstack.userservice.util.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class AppUserDTO {

    private Integer userId;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @ValidPassword
    private String password;

    @ValidPassword
    private String newPassword;

    @Email(message = "Email should be valid and contain at least one '@' and one '.'")
    private String email;

    private String firstName;
    private String lastName;
    private Boolean isVerified;
    private Boolean isActive;
    private Date dateCreated;
    private Date dateModified;
    private Date dateOfBirth;
    private Long phoneNumber;
    private String address;
    private String secretQuestion;
    private String secretAnswer;
    private String role;
    private int failedLoginAttempts;
    private Date lockTime;
}
