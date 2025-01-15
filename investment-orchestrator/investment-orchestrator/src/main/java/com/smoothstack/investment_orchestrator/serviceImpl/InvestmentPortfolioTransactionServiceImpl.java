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

package com.smoothstack.investment_orchestrator.serviceImpl;

import com.smoothstack.investment_orchestrator.dao.InvestmentPortfolioTransactionRepository;
import com.smoothstack.investment_orchestrator.dto.InvestmentPortfolioTransactionDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InvestmentPortfolioTransactionNotFoundException;
import com.smoothstack.investment_orchestrator.exception.custom.InvestmentPortfolioTransactionServiceException;
import com.smoothstack.investment_orchestrator.exception.general.PessimisticLockingException;
import com.smoothstack.investment_orchestrator.mapper.InvestmentPortfolioTransactionMapper;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolioTransaction;
import com.smoothstack.investment_orchestrator.service.InvestmentPortfolioTransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Primary
@Service
public class InvestmentPortfolioTransactionServiceImpl implements InvestmentPortfolioTransactionService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final InvestmentPortfolioTransactionMapper investmentPortfolioTransactionMapper;
    private final InvestmentPortfolioTransactionRepository investmentPortfolioTransactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvestmentPortfolioTransactionServiceImpl.class);

    @Override
    public List<InvestmentPortfolioTransactionDTO> getInvestmentPortfolioTransactionsByParams(Integer investmentPortfolioTransactionId) {
        if (investmentPortfolioTransactionId != null) {
            return getInvestmentPortfolioTransactionById(investmentPortfolioTransactionId);
        } else {
            return getAllInvestmentPortfolioTransactions();
        }
    }

    @Override
    public List<InvestmentPortfolioTransactionDTO> getAllInvestmentPortfolioTransactions() {
        try {
            logger.info("Fetching all investment portfolio transactions.");
            return investmentPortfolioTransactionRepository.findAll()
                    .stream()
                    .map(investmentPortfolioTransactionMapper::investmentPortfolioTransactionToInvestmentPortfolioTransactionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Error fetching all investment portfolio transactions: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Error fetching all investment portfolio transactions. Check the input data and try again", ex) {
            };
        }
    }

    @Override
    public List<InvestmentPortfolioTransactionDTO> getInvestmentPortfolioTransactionById(Integer investmentPortfolioTransactionId) {
        try {
            logger.info("Fetching investment portfolio transaction with ID: {}", investmentPortfolioTransactionId);
            return investmentPortfolioTransactionRepository.findByInvestmentPortfolioTransactionId(investmentPortfolioTransactionId)
                    .stream()
                    .map(investmentPortfolioTransactionMapper::investmentPortfolioTransactionToInvestmentPortfolioTransactionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Data access issue while fetching investment portfolio transaction: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Failed to fetch investment portfolio transaction due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error fetching investment portfolio transaction with ID {}: {}", investmentPortfolioTransactionId, ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Failed to fetch investment portfolio transaction. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestmentPortfolioTransactionDTO createInvestmentPortfolioTransaction(InvestmentPortfolioTransactionDTO investmentPortfolioTransactionDTO) {
        try {
            logger.info("Creating new investment portfolio transaction.");
            InvestmentPortfolioTransaction investmentPortfolioTransaction = investmentPortfolioTransactionMapper.investmentPortfolioTransactionDTOToInvestmentPortfolioTransaction(investmentPortfolioTransactionDTO);
            logger.info("Investment portfolio transaction to save: {}", investmentPortfolioTransaction);
            InvestmentPortfolioTransaction savedInvestmentPortfolioTransaction = investmentPortfolioTransactionRepository.save(investmentPortfolioTransaction);
            logger.info("Saving investment portfolio transaction: {}", savedInvestmentPortfolioTransaction);
            InvestmentPortfolioTransactionDTO savedInvestmentPortfolioTransactionDTO = investmentPortfolioTransactionMapper.investmentPortfolioTransactionToInvestmentPortfolioTransactionDTO(savedInvestmentPortfolioTransaction);
            logger.info(String.valueOf(investmentPortfolioTransactionDTO));
            logger.info("Investment portfolio transaction created successfully: {}", savedInvestmentPortfolioTransactionDTO);
            return savedInvestmentPortfolioTransactionDTO;
        } catch (DataAccessException ex) {
            logger.error("Data access issue while creating investment portfolio transaction: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Failed to create investment portfolio transaction due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error creating investment portfolio transaction: {}", ex.getMessage(), ex);
            logger.error("Failed to create investment portfolio transaction with data: {}", investmentPortfolioTransactionDTO);
            throw new InvestmentPortfolioTransactionServiceException("Failed to create investment portfolio transaction. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestmentPortfolioTransactionDTO updateInvestmentPortfolioTransactionById(Integer investmentPortfolioTransactionId, InvestmentPortfolioTransactionDTO updatedInvestmentPortfolioTransactionDTO) {
        try {
            logger.info("Updating investment portfolio transaction with ID: {}", investmentPortfolioTransactionId);
            InvestmentPortfolioTransaction existingInvestmentPortfolioTransaction = getExistingInvestmentPortfolioTransaction(investmentPortfolioTransactionId);
            InvestmentPortfolioTransaction updatedInvestmentPortfolioTransaction = investmentPortfolioTransactionMapper.investmentPortfolioTransactionDTOToInvestmentPortfolioTransaction(updatedInvestmentPortfolioTransactionDTO);
            updateExistingInvestmentPortfolioTransaction(existingInvestmentPortfolioTransaction, updatedInvestmentPortfolioTransaction);
            InvestmentPortfolioTransaction savedInvestmentPortfolioTransaction = investmentPortfolioTransactionRepository.save(existingInvestmentPortfolioTransaction);
            logger.info("Investment portfolio transaction with ID {} updated successfully", investmentPortfolioTransactionId);
            return investmentPortfolioTransactionMapper.investmentPortfolioTransactionToInvestmentPortfolioTransactionDTO(savedInvestmentPortfolioTransaction);
        } catch (EntityNotFoundException ex) {
            logger.error("Error updating investment portfolio transaction with ID {}: Investment portfolio transaction not found", investmentPortfolioTransactionId);
            throw new InvestmentPortfolioTransactionServiceException("Failed to update investment portfolio transaction due to an entity not found issue. Check the input data and try again.", ex);
        } catch (Exception ex) {
            logger.error("Error updating investment portfolio transaction with ID {}: {}", investmentPortfolioTransactionId, ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Failed to update investment portfolio transaction. Check the input data and try again.", ex);
        }
    }

    private InvestmentPortfolioTransaction getExistingInvestmentPortfolioTransaction(Integer investmentPortfolioTransactionId) {
        Optional<InvestmentPortfolioTransaction> optionalInvestmentPortfolioTransaction = investmentPortfolioTransactionRepository.findByInvestmentPortfolioTransactionId(investmentPortfolioTransactionId);
        if (optionalInvestmentPortfolioTransaction.isPresent()) {
            InvestmentPortfolioTransaction existingInvestmentPortfolioTransaction = optionalInvestmentPortfolioTransaction.get();
            try {
                entityManager.lock(existingInvestmentPortfolioTransaction, LockModeType.PESSIMISTIC_WRITE);
                return existingInvestmentPortfolioTransaction;
            } catch (Exception ex) {
                logger.error("Error acquiring pessimistic lock for investment portfolio transaction with ID {}: {}", investmentPortfolioTransactionId, ex.getMessage(), ex);
                throw new PessimisticLockingException("Failed to acquire pessimistic lock for investment portfolio transaction with ID: " + investmentPortfolioTransactionId, ex);
            }
        } else {
            throw new EntityNotFoundException("Investment portfolio transaction not found with ID: " + investmentPortfolioTransactionId);
        }
    }

    private void updateExistingInvestmentPortfolioTransaction(InvestmentPortfolioTransaction existingInvestmentPortfolioTransaction, InvestmentPortfolioTransaction updatedInvestmentPortfolioTransaction) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.map(updatedInvestmentPortfolioTransaction, existingInvestmentPortfolioTransaction);
    }

    @Override
    @Transactional
    public void deleteInvestmentPortfolioTransactionById(Integer investmentPortfolioTransactionId) {
        try {
            logger.info("Deleting investment portfolio transaction with ID: {}", investmentPortfolioTransactionId);
            Optional<InvestmentPortfolioTransaction> investmentPortfolioTransactionOptional = investmentPortfolioTransactionRepository.findById(investmentPortfolioTransactionId);
            if (investmentPortfolioTransactionOptional.isPresent()) {
                investmentPortfolioTransactionRepository.deleteById(investmentPortfolioTransactionId);
                logger.info("Investment portfolio transaction with ID {} deleted successfully", investmentPortfolioTransactionId);
            }
            else {
                throw new InvestmentPortfolioTransactionNotFoundException("Investment portfolio transaction not found with ID: " + investmentPortfolioTransactionId);
            }
        } catch (Exception ex) {
            logger.error("Error deleting investment portfolio transaction with ID {}: {}", investmentPortfolioTransactionId, ex.getMessage(), ex);
            throw new InvestmentPortfolioTransactionServiceException("Failed to delete investment portfolio transaction. Check the input data and try again.", ex);
        }
    }
}
