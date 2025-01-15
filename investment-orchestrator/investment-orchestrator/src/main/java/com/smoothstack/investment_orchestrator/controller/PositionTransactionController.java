package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.PositionTransactionDTO;
import com.smoothstack.investment_orchestrator.service.PositionTransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/positionTransaction")
public class PositionTransactionController {
    private final PositionTransactionService positionTransactionService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<PositionTransactionDTO>> getPositionTransactions(
            @RequestParam(required = false) Integer positionTransactionId) {

        List<PositionTransactionDTO> positionTransactions = positionTransactionService.getPositionTransactionsByParams(positionTransactionId);

        return ResponseEntity.ok(positionTransactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PositionTransactionDTO> createPositionTransaction(
            @Valid @RequestBody PositionTransactionDTO positionTransactionDTO
    ) {
        PositionTransactionDTO createdPositionTransaction = positionTransactionService.createPositionTransaction(positionTransactionDTO);

        return new ResponseEntity<>(createdPositionTransaction, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{positionTransactionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PositionTransactionDTO> updatePositionTransactionById(
            @PathVariable("positionTransactionId") Integer positionTransactionId,
            @Valid @RequestBody PositionTransactionDTO updatedPositionTransaction
    ) {
        PositionTransactionDTO updatedPositionTransactionDTO = positionTransactionService.updatePositionTransactionById(positionTransactionId, updatedPositionTransaction);

        return ResponseEntity.ok(updatedPositionTransactionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{positionTransactionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletePositionTransactionById(@PathVariable Integer positionTransactionId) {
        positionTransactionService.deletePositionTransactionById(positionTransactionId);

        return ResponseEntity.noContent().build();
    }
}
