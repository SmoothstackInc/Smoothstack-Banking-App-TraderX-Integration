/*
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

package com.smoothstack.investment_orchestrator.exception.custom;

import lombok.AllArgsConstructor;

/**
 * Exception for account user not found issues.
 */
@AllArgsConstructor
public class AccountUserNotFoundException extends RuntimeException {
    public AccountUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountUserNotFoundException(String message) {
        super(message);
    }
}
