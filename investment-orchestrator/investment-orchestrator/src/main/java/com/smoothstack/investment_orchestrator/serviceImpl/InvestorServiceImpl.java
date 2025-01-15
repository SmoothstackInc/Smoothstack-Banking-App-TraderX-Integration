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


import com.smoothstack.investment_orchestrator.dao.InvestorRepository;
import com.smoothstack.investment_orchestrator.dto.InvestorDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InvestorNotFoundException;
import com.smoothstack.investment_orchestrator.exception.custom.InvestorServiceException;
import com.smoothstack.investment_orchestrator.exception.general.PessimisticLockingException;
import com.smoothstack.investment_orchestrator.mapper.InvestorMapper;
import com.smoothstack.investment_orchestrator.model.Investor;
import com.smoothstack.investment_orchestrator.service.InvestorService;
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
public class InvestorServiceImpl implements InvestorService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final InvestorMapper investorMapper;
    private final InvestorRepository investorRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvestorServiceImpl.class);

    @Override
    public List<InvestorDTO> getInvestorsByParams(Integer investorId) {
        if (investorId != null) {
            return getInvestorById(investorId);
        } else {
            return getAllInvestors();
        }
    }

    @Override
    public List<InvestorDTO> getAllInvestors() {
        try {
            logger.info("Fetching all investors.");
            return investorRepository.findAll()
                    .stream()
                    .map(investorMapper::investorToInvestorDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Error fetching all investors: {}", ex.getMessage(), ex);
            throw new InvestorServiceException("Error fetching all investors. Check the input data and try again", ex) {
            };
        }
    }

    @Override
    public List<InvestorDTO> getInvestorById(Integer investorId) {
        try {
            logger.info("Fetching investor with ID: {}", investorId);
            return investorRepository.findByInvestorId(investorId)
                    .stream()
                    .map(investorMapper::investorToInvestorDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Data access issue while fetching investor: {}", ex.getMessage(), ex);
            throw new InvestorServiceException("Failed to fetch investor due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error fetching investor with ID {}: {}", investorId, ex.getMessage(), ex);
            throw new InvestorServiceException("Failed to fetch investor. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestorDTO createInvestor(InvestorDTO investorDTO) {
        try {
            logger.info("Creating new investor.");
            Investor investor = investorMapper.investorDTOToInvestor(investorDTO);
            logger.info("Investor to save: {}", investor);
            Investor savedInvestor = investorRepository.save(investor);
            logger.info("Saving investor: {}", savedInvestor);
            InvestorDTO savedInvestorDTO = investorMapper.investorToInvestorDTO(savedInvestor);
            logger.info(String.valueOf(investorDTO));
            logger.info("Investor created successfully: {}", savedInvestorDTO);
            return savedInvestorDTO;
        } catch (DataAccessException ex) {
            logger.error("Data access issue while creating accountUser: {}", ex.getMessage(), ex);
            throw new InvestorServiceException("Failed to create investor due to a data access issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error creating investor: {}", ex.getMessage(), ex);
            logger.error("Failed to create investor with data: {}", investorDTO);
            throw new InvestorServiceException("Failed to create investor. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestorDTO updateInvestorById(Integer investorId, InvestorDTO updatedInvestorDTO) {
        try {
            logger.info("Updating investor with ID: {}", investorId);
            Investor existingInvestor = getExistingInvestor(investorId);
            Investor updatedInvestor = investorMapper.investorDTOToInvestor(updatedInvestorDTO);
            updateExistingInvestor(existingInvestor, updatedInvestor);
            Investor savedInvestor = investorRepository.save(existingInvestor);
            logger.info("Investor with ID {} updated successfully", investorId);
            return investorMapper.investorToInvestorDTO(savedInvestor);
        } catch (EntityNotFoundException ex) {
            logger.error("Error updating investor with ID {}: Investor not found", investorId);
            throw new InvestorServiceException("Failed to update investor due to an entity not found issue. Check the input data and try again.", ex);
        } catch (Exception ex) {
            logger.error("Error updating investor with ID {}: {}", investorId, ex.getMessage(), ex);
            throw new InvestorServiceException("Failed to update investor. Check the input data and try again.", ex);
        }
    }

    private Investor getExistingInvestor(Integer investorId) {
        Optional<Investor> optionalInvestor = investorRepository.findByInvestorId(investorId);
        if (optionalInvestor.isPresent()) {
            Investor existingInvestor = optionalInvestor.get();
            try {
                entityManager.lock(existingInvestor, LockModeType.PESSIMISTIC_WRITE);
                return existingInvestor;
            } catch (Exception ex) {
                logger.error("Error acquiring pessimistic lock for investor with ID {}: {}", investorId, ex.getMessage(), ex);
                throw new PessimisticLockingException("Failed to acquire pessimistic lock for investor with ID: " + investorId, ex);
            }
        } else {
            throw new EntityNotFoundException("Investor not found with ID: " + investorId);
        }
    }

    private void updateExistingInvestor(Investor existingInvestor, Investor updatedInvestor) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.map(updatedInvestor, existingInvestor);
    }

    @Override
    @Transactional
    public void deleteInvestorById(Integer investorId) {
        try {
            logger.info("Deleting investor with ID: {}", investorId);
            Optional<Investor> buyerOptional = investorRepository.findById(investorId);
            if (buyerOptional.isPresent()) {
                investorRepository.deleteById(investorId);
                logger.info("Investor with ID {} deleted successfully", investorId);
            }
            else {
                throw new InvestorNotFoundException("Investor not found with ID: " + investorId);
            }
        } catch (Exception ex) {
            logger.error("Error deleting investor with ID {}: {}", investorId, ex.getMessage(), ex);
            throw new InvestorServiceException("Failed to delete investor. Check the input data and try again.", ex);
        }
    }
}
