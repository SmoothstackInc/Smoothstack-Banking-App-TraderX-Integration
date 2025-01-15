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

package com.smoothstack.investment_orchestrator.service;

import com.smoothstack.investment_orchestrator.dto.AccountUserDTO;

import java.util.List;

public interface AccountUserService {

    List<AccountUserDTO> getAccountUserByParams(Integer accountUserId);
    List<AccountUserDTO> getAllAccountUsers();
    List<AccountUserDTO> getAccountUserById(Integer accountUserId);
    AccountUserDTO createAccountUser(AccountUserDTO accountUserDTO);
    AccountUserDTO updateAccountUserById(Integer accountUserId, AccountUserDTO updatedAccountUserDTO);
    void deleteAccountUserById(Integer accountUserId);
}
