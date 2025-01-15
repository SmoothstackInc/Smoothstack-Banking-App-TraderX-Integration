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

import com.smoothstack.investment_orchestrator.dto.PositionTransactionDTO;
import com.smoothstack.investment_orchestrator.exception.custom.PositionTransactionNotFoundException;
import com.smoothstack.investment_orchestrator.exception.custom.PositionTransactionServiceException;
import com.smoothstack.investment_orchestrator.exception.general.PessimisticLockingException;
import com.smoothstack.investment_orchestrator.mapper.PositionTransactionMapper;
import com.smoothstack.investment_orchestrator.model.PositionTransaction;
import com.smoothstack.investment_orchestrator.dao.PositionTransactionRepository;
import com.smoothstack.investment_orchestrator.service.PositionTransactionService;
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
public class PositionTransactionServiceImpl implements PositionTransactionService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final PositionTransactionMapper positionTransactionMapper;
    private final PositionTransactionRepository positionTransactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(PositionTransactionServiceImpl.class);

    @Override
    public List<PositionTransactionDTO> getPositionTransactionsByParams(Integer positionTransactionId) {
        if (positionTransactionId != null) {
            return getPositionTransactionById(positionTransactionId);
        } else {
            return getAllPositionTransactions();
        }
    }

    @Override
    public List<PositionTransactionDTO> getAllPositionTransactions() {
        try {
            logger.info("Fetching all position transactions.");
            return positionTransactionRepository.findAll()
                    .stream()
                    .map(positionTransactionMapper::positionTransactionToPositionTransactionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Error fetching all position transactions: {}", ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Error fetching all position transactions. Check the input data and try again", ex) {
            };
        }
    }

    @Override
    public List<PositionTransactionDTO> getPositionTransactionById(Integer positionTransactionId) {
        try {
            logger.info("Fetching position transaction with ID: {}", positionTransactionId);
            return positionTransactionRepository.findByPositionTransactionId(positionTransactionId)
                    .stream()
                    .map(positionTransactionMapper::positionTransactionToPositionTransactionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Data access issue while fetching position transaction: {}", ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Failed to fetch position transaction due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error fetching position transaction with ID {}: {}", positionTransactionId, ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Failed to fetch position transaction. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public PositionTransactionDTO createPositionTransaction(PositionTransactionDTO positionTransactionDTO) {
        try {
            logger.info("Creating new position transaction.");
            PositionTransaction positionTransaction = positionTransactionMapper.positionTransactionDTOToPositionTransaction(positionTransactionDTO);
            logger.info("Position transaction to save: {}", positionTransaction);
            PositionTransaction savedPositionTransaction = positionTransactionRepository.save(positionTransaction);
            logger.info("Saving position transaction: {}", savedPositionTransaction);
            PositionTransactionDTO savedPositionTransactionDTO = positionTransactionMapper.positionTransactionToPositionTransactionDTO(savedPositionTransaction);
            logger.info(String.valueOf(positionTransactionDTO));
            logger.info("Position transaction created successfully: {}", savedPositionTransactionDTO);
            return savedPositionTransactionDTO;
        } catch (DataAccessException ex) {
            logger.error("Data access issue while creating position transaction: {}", ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Failed to create position transaction due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error creating position transaction: {}", ex.getMessage(), ex);
            logger.error("Failed to create position transaction with data: {}", positionTransactionDTO);
            throw new PositionTransactionServiceException("Failed to create position transaction. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public PositionTransactionDTO updatePositionTransactionById(Integer positionTransactionId, PositionTransactionDTO updatedPositionTransactionDTO) {
        try {
            logger.info("Updating position transaction with ID: {}", positionTransactionId);
            PositionTransaction existingPositionTransaction = getExistingPositionTransaction(positionTransactionId);
            PositionTransaction updatedPositionTransaction = positionTransactionMapper.positionTransactionDTOToPositionTransaction(updatedPositionTransactionDTO);
            updateExistingPositionTransaction(existingPositionTransaction, updatedPositionTransaction);
            PositionTransaction savedPositionTransaction = positionTransactionRepository.save(existingPositionTransaction);
            logger.info("Position transaction with ID {} updated successfully", positionTransactionId);
            return positionTransactionMapper.positionTransactionToPositionTransactionDTO(savedPositionTransaction);
        } catch (EntityNotFoundException ex) {
            logger.error("Error updating position transaction with ID {}: Position not found", positionTransactionId);
            throw new PositionTransactionServiceException("Failed to update position transaction due to an entity not found issue. Check the input data and try again.", ex);
        } catch (Exception ex) {
            logger.error("Error updating position transaction with ID {}: {}", positionTransactionId, ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Failed to update position transaction. Check the input data and try again.", ex);
        }
    }

    private PositionTransaction getExistingPositionTransaction(Integer positionTransactionId) {
        Optional<PositionTransaction> optionalPositionTransaction = positionTransactionRepository.findByPositionTransactionId(positionTransactionId);
        if (optionalPositionTransaction.isPresent()) {
            PositionTransaction existingPositionTransaction = optionalPositionTransaction.get();
            try {
                entityManager.lock(existingPositionTransaction, LockModeType.PESSIMISTIC_WRITE);
                return existingPositionTransaction;
            } catch (Exception ex) {
                logger.error("Error acquiring pessimistic lock for position transaction with ID {}: {}", positionTransactionId, ex.getMessage(), ex);
                throw new PessimisticLockingException("Failed to acquire pessimistic lock for position transaction with ID: " + positionTransactionId, ex);
            }
        } else {
            throw new EntityNotFoundException("Position share not found with ID: " + positionTransactionId);
        }
    }

    private void updateExistingPositionTransaction(PositionTransaction existingPositionTransaction, PositionTransaction updatedPositionTransaction) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.map(updatedPositionTransaction, existingPositionTransaction);
    }

    @Override
    @Transactional
    public void deletePositionTransactionById(Integer positionTransactionId) {
        try {
            logger.info("Deleting position transaction with ID: {}", positionTransactionId);
            Optional<PositionTransaction> securityTransactionOptional = positionTransactionRepository.findById(positionTransactionId);
            if (securityTransactionOptional.isPresent()) {
                positionTransactionRepository.deleteById(positionTransactionId);
                logger.info("Position transaction with ID {} deleted successfully", positionTransactionId);
            }
            else {
                throw new PositionTransactionNotFoundException("Position transaction not found with ID: " + positionTransactionId);
            }
        } catch (Exception ex) {
            logger.error("Error deleting position transaction with ID {}: {}", positionTransactionId, ex.getMessage(), ex);
            throw new PositionTransactionServiceException("Failed to delete position transaction. Check the input data and try again.", ex);
        }
    }
}
