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

package com.smoothstack.userservice.util;

import com.smoothstack.userservice.model.AppUser;
import jakarta.validation.ConstraintViolation;

public class ViolationFormatter {

    public static String formatConstraintViolation(AppUser item, ConstraintViolation<?> violation) {
        return String.format(
                "Username: %s, Email: %s, Error: %s: %s",
                item != null ? item.getUsername() : "N/A",
                item != null ? item.getEmail() : "N/A",
                violation.getPropertyPath(),
                violation.getMessage()
        );
    }

    public static String formatConstraintViolation(String username, String email, ConstraintViolation<?> violation) {
        return String.format(
                "Username: %s, Email: %s, Error: %s: %s",
                username != null ? username : "N/A",
                email != null ? email : "N/A",
                violation.getPropertyPath(),
                violation.getMessage()
        );
    }
}
