package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.InvestmentPortfolioDTO;
import com.smoothstack.investment_orchestrator.service.InvestmentPortfolioService;
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
@RequestMapping("/api/v1/investmentPortfolio")
public class InvestmentPortfolioController {
    private final InvestmentPortfolioService investmentPortfolioService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<InvestmentPortfolioDTO>> getInvestmentPortfolios(
            @RequestParam(required = false) Integer investmentPortfolioId) {

        List<InvestmentPortfolioDTO> investmentPortfolios = investmentPortfolioService.getInvestmentPortfoliosByParams(investmentPortfolioId);

        return ResponseEntity.ok(investmentPortfolios);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestmentPortfolioDTO> createInvestmentPortfolio(
            @Valid @RequestBody InvestmentPortfolioDTO investmentPortfolioDTO
    ) {
        InvestmentPortfolioDTO createdInvestmentPortfolio = investmentPortfolioService.createInvestmentPortfolio(investmentPortfolioDTO);

        return new ResponseEntity<>(createdInvestmentPortfolio, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{investmentPortfolioId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestmentPortfolioDTO> updateInvestmentPortfolioById(
            @PathVariable("investmentPortfolioId") Integer investmentPortfolioId,
            @Valid @RequestBody InvestmentPortfolioDTO updatedInvestmentPortfolio
    ) {
        InvestmentPortfolioDTO updatedInvestmentPortfolioDTO = investmentPortfolioService.updateInvestmentPortfolioById(investmentPortfolioId, updatedInvestmentPortfolio);

        return ResponseEntity.ok(updatedInvestmentPortfolioDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{investmentPortfolioId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteInvestmentPortfolioById(@PathVariable Integer investmentPortfolioId) {
        investmentPortfolioService.deleteInvestmentPortfolioById(investmentPortfolioId);

        return ResponseEntity.noContent().build();
    }
}
