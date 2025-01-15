package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.PositionDTO;
import com.smoothstack.investment_orchestrator.dto.TradeRequestDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InsufficientFundsException;
import com.smoothstack.investment_orchestrator.exception.custom.PositionServiceException;
import com.smoothstack.investment_orchestrator.service.PositionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/position")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class PositionController {
    private final PositionService positionService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<PositionDTO>> getPositions(
            @RequestParam(required = false) Integer positionId) {

        List<PositionDTO> positions = positionService.getPositionsByParams(positionId);

        return ResponseEntity.ok(positions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PositionDTO> createPosition(
            @Valid @RequestBody PositionDTO positionDTO
    ) {
        PositionDTO createdPosition = positionService.createPosition(positionDTO);

        return new ResponseEntity<>(createdPosition, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{positionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<PositionDTO> updatePositionById(
            @PathVariable("securityId") Integer positionId,
            @Valid @RequestBody PositionDTO updatedPosition
    ) {
        PositionDTO updatedPositionDTO = positionService.updatePositionById(positionId, updatedPosition);

        return ResponseEntity.ok(updatedPositionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{positionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletePositionById(@PathVariable Integer positionId) {
        positionService.deletePositionById(positionId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/trade")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<ApiResponse> tradeStock(@RequestBody TradeRequestDTO tradeRequestDTO) {
        try {
            positionService.processTrade(tradeRequestDTO);
            ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "Trade processed and sent for execution.");
            return ResponseEntity.ok(response);
        } catch (InsufficientFundsException e) {
            log.warn("Insufficient funds for trade request: {}", e.getMessage());
            throw e;
        } catch (PositionServiceException e) {
            log.error("Error occurred during trade: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred during trade: {}", e.getMessage(), e);
            throw e;
        }
    }
}