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

import com.smoothstack.investment_orchestrator.dao.InvestmentPortfolioRepository;
import com.smoothstack.investment_orchestrator.dto.InvestmentPortfolioDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InvestmentPortfolioNotFoundException;
import com.smoothstack.investment_orchestrator.exception.custom.InvestmentPortfolioServiceException;
import com.smoothstack.investment_orchestrator.exception.general.PessimisticLockingException;
import com.smoothstack.investment_orchestrator.mapper.InvestmentPortfolioMapper;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import com.smoothstack.investment_orchestrator.service.InvestmentPortfolioService;
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
public class InvestmentPortfolioServiceImpl implements InvestmentPortfolioService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final InvestmentPortfolioMapper investmentPortfolioMapper;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private static final Logger logger = LoggerFactory.getLogger(InvestmentPortfolioServiceImpl.class);

    @Override
    public List<InvestmentPortfolioDTO> getInvestmentPortfoliosByParams(Integer investmentPortfolioId) {
        if (investmentPortfolioId != null) {
            return getInvestmentPortfolioById(investmentPortfolioId);
        } else {
            return getAllInvestmentPortfolios();
        }
    }

    @Override
    public List<InvestmentPortfolioDTO> getAllInvestmentPortfolios() {
        try {
            logger.info("Fetching all investment portfolios.");
            return investmentPortfolioRepository.findAll()
                    .stream()
                    .map(investmentPortfolioMapper::investmentPortfolioToInvestmentPortfolioDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Error fetching all investment portfolios: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioServiceException("Error fetching all investment portfolios. Check the input " +
                    "data and try again", ex) {
            };
        }
    }

    @Override
    public List<InvestmentPortfolioDTO> getInvestmentPortfolioById(Integer investmentPortfolioId) {
        try {
            logger.info("Fetching account with ID: {}", investmentPortfolioId);
            return investmentPortfolioRepository.findByInvestmentPortfolioId(investmentPortfolioId)
                    .stream()
                    .map(investmentPortfolioMapper::investmentPortfolioToInvestmentPortfolioDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Data access issue while fetching investment portfolio: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioServiceException("Failed to fetch investment portfolio due to a data access " +
                    "issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error fetching investment portfolio with ID {}: {}", investmentPortfolioId, ex.getMessage()
                    , ex);
            throw new InvestmentPortfolioServiceException("Failed to fetch investment portfolio. Check the input data" +
                    " and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestmentPortfolioDTO createInvestmentPortfolio(InvestmentPortfolioDTO investmentPortfolioDTO) {
        try {
            logger.info("Creating new investment portfolio.");
            InvestmentPortfolio investmentPortfolio =
                    investmentPortfolioMapper.investmentPortfolioDTOToInvestmentPortfolio(investmentPortfolioDTO);
            logger.info("Investment portfolio to save: {}", investmentPortfolio);
            InvestmentPortfolio savedInvestmentPortfolio = investmentPortfolioRepository.save(investmentPortfolio);
            logger.info("Saving investment portfolio: {}", savedInvestmentPortfolio);
            InvestmentPortfolioDTO savedInvestmentPortfolioDTO =
                    investmentPortfolioMapper.investmentPortfolioToInvestmentPortfolioDTO(savedInvestmentPortfolio);
            logger.info(String.valueOf(investmentPortfolioDTO));
            logger.info("Investment portfolio created successfully: {}", savedInvestmentPortfolioDTO);
            return savedInvestmentPortfolioDTO;
        } catch (DataAccessException ex) {
            logger.error("Data access issue while creating investment portfolio: {}", ex.getMessage(), ex);
            throw new InvestmentPortfolioServiceException("Failed to create investment portfolio due to a data access" +
                    " issue. Please try again later.", ex);
        } catch (Exception ex) {
            logger.error("Error creating investment portfolio: {}", ex.getMessage(), ex);
            logger.error("Failed to create investment portfolio with data: {}", investmentPortfolioDTO);
            throw new InvestmentPortfolioServiceException("Failed to create investment portfolio. Check the input " +
                    "data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public InvestmentPortfolioDTO updateInvestmentPortfolioById(Integer investmentPortfolioId,
                                                                InvestmentPortfolioDTO updatedInvestmentPortfolioDTO) {
        try {
            logger.info("Updating investment portfolio with ID: {}", investmentPortfolioId);
            InvestmentPortfolio existingInvestmentPortfolio = getExistingInvestmentPortfolio(investmentPortfolioId);
            InvestmentPortfolio updatedInvestmentPortfolio =
                    investmentPortfolioMapper.investmentPortfolioDTOToInvestmentPortfolio(updatedInvestmentPortfolioDTO);
            updateExistingInvestmentPortfolio(existingInvestmentPortfolio, updatedInvestmentPortfolio);
            InvestmentPortfolio savedInvestmentPortfolio =
                    investmentPortfolioRepository.save(existingInvestmentPortfolio);
            logger.info("Investment portfolio with ID {} updated successfully", investmentPortfolioId);
            return investmentPortfolioMapper.investmentPortfolioToInvestmentPortfolioDTO(savedInvestmentPortfolio);
        } catch (EntityNotFoundException ex) {
            logger.error("Error updating investment portfolio with ID {}: Investment portfolio not found",
                    investmentPortfolioId);
            throw new InvestmentPortfolioServiceException("Failed to update investment portfolio due to an entity not" +
                    " found issue. Check the input data and try again.", ex);
        } catch (Exception ex) {
            logger.error("Error updating investment portfolio with ID {}: {}", investmentPortfolioId, ex.getMessage()
                    , ex);
            throw new InvestmentPortfolioServiceException("Failed to update investment portfolio. Check the input " +
                    "data and try again.", ex);
        }
    }

    private InvestmentPortfolio getExistingInvestmentPortfolio(Integer investmentPortfolioId) {
        Optional<InvestmentPortfolio> optionalInvestmentPortfolio =
                investmentPortfolioRepository.findByInvestmentPortfolioId(investmentPortfolioId);
        if (optionalInvestmentPortfolio.isPresent()) {
            InvestmentPortfolio existingInvestmentPortfolio = optionalInvestmentPortfolio.get();
            try {
                entityManager.lock(existingInvestmentPortfolio, LockModeType.PESSIMISTIC_WRITE);
                return existingInvestmentPortfolio;
            } catch (Exception ex) {
                logger.error("Error acquiring pessimistic lock for investment portfolio with ID {}: {}",
                        investmentPortfolioId, ex.getMessage(), ex);
                throw new PessimisticLockingException("Failed to acquire pessimistic lock for investment portfolio " +
                        "with ID: " + investmentPortfolioId, ex);
            }
        } else {
            throw new EntityNotFoundException("Investment portfolio not found with ID: " + investmentPortfolioId);
        }
    }

    private void updateExistingInvestmentPortfolio(InvestmentPortfolio existingInvestmentPortfolio,
                                                   InvestmentPortfolio updatedInvestmentPortfolio) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.map(updatedInvestmentPortfolio, existingInvestmentPortfolio);
    }

    @Override
    @Transactional
    public void deleteInvestmentPortfolioById(Integer investmentPortfolioId) {
        try {
            logger.info("Deleting investment portfolio with ID: {}", investmentPortfolioId);
            Optional<InvestmentPortfolio> fundOptional = investmentPortfolioRepository.findById(investmentPortfolioId);
            if (fundOptional.isPresent()) {
                investmentPortfolioRepository.deleteById(investmentPortfolioId);
                logger.info("Investment portfolio with ID {} deleted successfully", investmentPortfolioId);
            } else {
                throw new InvestmentPortfolioNotFoundException("Investment portfolio not found with ID: " + investmentPortfolioId);
            }
        } catch (Exception ex) {
            logger.error("Error deleting investment portfolio with ID {}: {}", investmentPortfolioId, ex.getMessage()
                    , ex);
            throw new InvestmentPortfolioServiceException("Failed to delete investment portfolio. Check the input " +
                    "data and try again.", ex);
        }
    }
}