package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.InvestmentPortfolioTransactionDTO;
import com.smoothstack.investment_orchestrator.service.InvestmentPortfolioTransactionService;
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
@RequestMapping("/api/v1/investmentPortfolioTransaction")
public class InvestmentPortfolioTransactionController {
    private final InvestmentPortfolioTransactionService investmentPortfolioTransactionService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<InvestmentPortfolioTransactionDTO>> getInvestmentPortfolioTransactions(
            @RequestParam(required = false) Integer investmentPortfolioTransactionId) {

        List<InvestmentPortfolioTransactionDTO> investmentPortfolioTransactions = investmentPortfolioTransactionService.getInvestmentPortfolioTransactionsByParams(investmentPortfolioTransactionId);

        return ResponseEntity.ok(investmentPortfolioTransactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestmentPortfolioTransactionDTO> createInvestmentPortfolioTransaction(
            @Valid @RequestBody InvestmentPortfolioTransactionDTO investmentPortfolioTransactionDTO
    ) {
        InvestmentPortfolioTransactionDTO createdInvestmentPortfolioTransaction = investmentPortfolioTransactionService.createInvestmentPortfolioTransaction(investmentPortfolioTransactionDTO);

        return new ResponseEntity<>(createdInvestmentPortfolioTransaction, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{investmentPortfolioTransactionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestmentPortfolioTransactionDTO> updateInvestmentPortfolioTransactionById(
            @PathVariable("investmentPortfolioTransactionId") Integer investmentPortfolioTransactionId,
            @Valid @RequestBody InvestmentPortfolioTransactionDTO updatedInvestmentPortfolioTransaction
    ) {
        InvestmentPortfolioTransactionDTO updatedInvestmentPortfolioTransactionDTO = investmentPortfolioTransactionService.updateInvestmentPortfolioTransactionById(investmentPortfolioTransactionId, updatedInvestmentPortfolioTransaction);

        return ResponseEntity.ok(updatedInvestmentPortfolioTransactionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{investmentPortfolioTransactionId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteInvestmentPortfolioTransactionById(@PathVariable Integer investmentPortfolioTransactionId) {
        investmentPortfolioTransactionService.deleteInvestmentPortfolioTransactionById(investmentPortfolioTransactionId);

        return ResponseEntity.noContent().build();
    }
}
