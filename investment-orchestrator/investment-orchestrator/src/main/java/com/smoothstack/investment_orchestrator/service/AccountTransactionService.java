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

import com.smoothstack.investment_orchestrator.dto.AccountTransactionDTO;

import java.util.List;

public interface AccountTransactionService {

    List<AccountTransactionDTO> getAccountTransactionsByParams(Integer accountTransactionId);
    List<AccountTransactionDTO> getAllAccountTransactions();
    List<AccountTransactionDTO> getAccountTransactionById(Integer accountTransactionId);
    AccountTransactionDTO createAccountTransaction(AccountTransactionDTO accountTransactionDTO);
    AccountTransactionDTO updateAccountTransactionById(Integer accountTransactionId, AccountTransactionDTO updatedAccountTransactionDTO);
    void deleteAccountTransactionById(Integer accountTransactionId);
}
