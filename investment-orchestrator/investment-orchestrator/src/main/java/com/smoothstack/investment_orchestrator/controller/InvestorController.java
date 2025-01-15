package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.InvestorDTO;
import com.smoothstack.investment_orchestrator.service.InvestorService;
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
@RequestMapping("/api/v1/investor")
public class InvestorController {
    public final InvestorService investorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<InvestorDTO>> getInvestor(
            @RequestParam(required = false) Integer investorId) {

        List<InvestorDTO> investor = investorService.getInvestorsByParams(investorId);

        return ResponseEntity.ok(investor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestorDTO> createInvestor(
            @Valid @RequestBody InvestorDTO investorDTO
    ) {
        InvestorDTO createdInvestor = investorService.createInvestor(investorDTO);

        return new ResponseEntity<>(createdInvestor, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{investorId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<InvestorDTO> updateInvestorById(
            @PathVariable("accountUserId") Integer investorId,
            @Valid @RequestBody InvestorDTO updatedInvestor
    ) {
        InvestorDTO updatedInvestorDTO = investorService.updateInvestorById(investorId, updatedInvestor);

        return ResponseEntity.ok(updatedInvestorDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{investorId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteInvestorById(@PathVariable Integer investorId) {
        investorService.deleteInvestorById(investorId);

        return ResponseEntity.noContent().build();
    }
}
