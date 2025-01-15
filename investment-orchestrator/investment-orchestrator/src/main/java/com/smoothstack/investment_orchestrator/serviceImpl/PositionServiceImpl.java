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
import com.smoothstack.investment_orchestrator.dao.PositionRepository;
import com.smoothstack.investment_orchestrator.dao.PositionTransactionRepository;
import com.smoothstack.investment_orchestrator.dto.PositionDTO;
import com.smoothstack.investment_orchestrator.dto.TradeRequestDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InsufficientFundsException;
import com.smoothstack.investment_orchestrator.exception.custom.PositionNotFoundException;
import com.smoothstack.investment_orchestrator.exception.custom.PositionServiceException;
import com.smoothstack.investment_orchestrator.exception.general.PessimisticLockingException;
import com.smoothstack.investment_orchestrator.helpers.TradeHelpers;
import com.smoothstack.investment_orchestrator.mapper.PositionMapper;
import com.smoothstack.investment_orchestrator.mapper.TradeRequestMapper;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import com.smoothstack.investment_orchestrator.model.Position;
import com.smoothstack.investment_orchestrator.model.TradeOrder;
import com.smoothstack.investment_orchestrator.model.TradeSide;
import com.smoothstack.investment_orchestrator.service.PositionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
public class PositionServiceImpl implements PositionService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final PositionMapper positionMapper;
    private final TradeRequestMapper tradeRequestMapper;
    private final PositionRepository positionRepository;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private final PositionTransactionRepository positionTransactionRepository;
    private final RestTemplate restTemplate;
    @Value("${trade-service.url}")
    private final String tradeServiceURL;
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);
    private final TradeHelpers th;

    public PositionServiceImpl(EntityManager entityManager,
                               PositionMapper positionMapper,
                               TradeRequestMapper tradeRequestMapper,
                               PositionRepository positionRepository,
                               InvestmentPortfolioRepository investmentPortfolioRepository,
                               PositionTransactionRepository positionTransactionRepository,
                               RestTemplate restTemplate,
                               @Value("${trade-service.url}") String tradeServiceURL, TradeHelpers th) {
        this.entityManager = entityManager;
        this.positionMapper = positionMapper;
        this.tradeRequestMapper = tradeRequestMapper;
        this.positionRepository = positionRepository;
        this.investmentPortfolioRepository = investmentPortfolioRepository;
        this.positionTransactionRepository = positionTransactionRepository;
        this.restTemplate = restTemplate;
        this.tradeServiceURL = tradeServiceURL;
        this.th = th;
    }


    @Override
    public List<PositionDTO> getPositionsByParams(Integer positionId) {
        if (positionId != null) {
            return getPositionById(positionId);
        } else {
            return getAllPositions();
        }
    }

    @Override
    public List<PositionDTO> getAllPositions() {
        try {
            logger.info("Fetching all positions.");
            return positionRepository.findAll()
                    .stream()
                    .map(positionMapper::positionToPositionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Error fetching all positions: {}", ex.getMessage(), ex);
            throw new PositionServiceException("Error fetching all positions. Check the input data and try again", ex) {
            };
        }
    }

    @Override
    public List<PositionDTO> getPositionById(Integer positionId) {
        try {
            logger.info("Fetching position with ID: {}", positionId);
            return positionRepository.findByPositionId(positionId)
                    .stream()
                    .map(positionMapper::positionToPositionDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error("Data access issue while fetching position: {}", ex.getMessage(), ex);
            throw new PositionServiceException("Failed to fetch position due to a data access issue. Please try again" +
                    " later.", ex);
        } catch (Exception ex) {
            logger.error("Error fetching position with ID {}: {}", positionId, ex.getMessage(), ex);
            throw new PositionServiceException("Failed to fetch position. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public PositionDTO createPosition(PositionDTO positionDTO) {
        try {
            logger.info("Creating new position.");
            Position position = positionMapper.positionDTOToPosition(positionDTO);
            logger.info("Position to save: {}", position);
            Position savedPosition = positionRepository.save(position);
            logger.info("Saving position: {}", savedPosition);
            PositionDTO savedPositionDTO = positionMapper.positionToPositionDTO(savedPosition);
            logger.info(String.valueOf(positionDTO));
            logger.info("Position created successfully: {}", savedPositionDTO);
            return savedPositionDTO;
        } catch (DataAccessException ex) {
            logger.error("Data access issue while creating position: {}", ex.getMessage(), ex);
            throw new PositionServiceException("Failed to create position due to a data access issue. Please try " +
                    "again later.", ex);
        } catch (Exception ex) {
            logger.error("Error creating position: {}", ex.getMessage(), ex);
            logger.error("Failed to create position with data: {}", positionDTO);
            throw new PositionServiceException("Failed to create position. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public PositionDTO updatePositionById(Integer positionId, PositionDTO updatedPositionDTO) {
        try {
            logger.info("Updating position with ID: {}", positionId);
            Position existingPosition = getExistingPosition(positionId);
            Position updatedPosition = positionMapper.positionDTOToPosition(updatedPositionDTO);
            updateExistingPosition(existingPosition, updatedPosition);
            Position savedPosition = positionRepository.save(existingPosition);
            logger.info("Position with ID {} updated successfully", positionId);
            return positionMapper.positionToPositionDTO(savedPosition);
        } catch (EntityNotFoundException ex) {
            logger.error("Error updating position with ID {}: Position not found", positionId);
            throw new PositionServiceException("Failed to update position due to an entity not found issue. Check the" +
                    " input data and try again.", ex);
        } catch (Exception ex) {
            logger.error("Error updating position with ID {}: {}", positionId, ex.getMessage(), ex);
            throw new PositionServiceException("Failed to update position. Check the input data and try again.", ex);
        }
    }

    private Position getExistingPosition(Integer positionId) {
        Optional<Position> optionalPosition = positionRepository.findByPositionId(positionId);
        if (optionalPosition.isPresent()) {
            Position existingPosition = optionalPosition.get();
            try {
                entityManager.lock(existingPosition, LockModeType.PESSIMISTIC_WRITE);
                return existingPosition;
            } catch (Exception ex) {
                logger.error("Error acquiring pessimistic lock for position with ID {}: {}", positionId,
                        ex.getMessage(), ex);
                throw new PessimisticLockingException("Failed to acquire pessimistic lock for position with ID: " + positionId, ex);
            }
        } else {
            throw new EntityNotFoundException("Position not found with ID: " + positionId);
        }
    }

    private void updateExistingPosition(Position existingPosition, Position updatedPosition) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.map(updatedPosition, existingPosition);
    }

    @Override
    @Transactional
    public void deletePositionById(Integer positionId) {
        try {
            logger.info("Deleting position with ID: {}", positionId);
            Optional<Position> positionOptional = positionRepository.findById(positionId);
            if (positionOptional.isPresent()) {
                positionRepository.deleteById(positionId);
                logger.info("Position with ID {} deleted successfully", positionId);
            } else {
                throw new PositionNotFoundException("Position not found with ID: " + positionId);
            }
        } catch (Exception ex) {
            logger.error("Error deleting position with ID {}: {}", positionId, ex.getMessage(), ex);
            throw new PositionServiceException("Failed to delete position. Check the input data and try again.", ex);
        }
    }

    @Override
    @Transactional
    public void processTrade(TradeRequestDTO tradeRequestDTO) {
        InvestmentPortfolio portfolio = investmentPortfolioRepository
                .findById(tradeRequestDTO.getInvestmentPortfolioId())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        try {
            th.handleTransaction(portfolio, tradeRequestDTO, tradeRequestDTO.getSide().toUpperCase());
            logger.info("Trade processed successfully for trade request: {}", tradeRequestDTO);
        } catch (InsufficientFundsException ex) {
            logger.warn("Insufficient funds for trade request: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error processing trade: {}", ex.getMessage(), ex);
            throw new PositionServiceException("Failed to process trade. Check the input data and try again.", ex);
        }
    }

    private void sendTradeOrder(TradeRequestDTO tradeRequestDTO) {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setId("");
        tradeOrder.setSecurity(tradeRequestDTO.getTicker());
        tradeOrder.setQuantity(tradeRequestDTO.getQuantity());
        tradeOrder.setAccountId(tradeRequestDTO.getInvestmentPortfolioId());
        tradeOrder.setSide(TradeSide.fromString(tradeRequestDTO.getSide()));

        logger.info("Sending trade order: {}", tradeOrder);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TradeOrder> requestEntity = new HttpEntity<>(tradeOrder, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tradeServiceURL, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Trade order successfully sent to trade service.");
            } else {
                logger.error("Failed to send trade order: " + response.getBody());
            }
        } catch (RestClientException e) {
            logger.error("Error occurred while sending trade order to trade service: " + e.getMessage());
            throw new RuntimeException("Error in sending trade order to trade service", e);
        }
    }
}