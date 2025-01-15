package com.smoothstack.investment_orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvestmentPortfolioDTO {

    private Integer investmentPortfolioId;

    @NotNull
    private Integer investorId;

    @NotBlank
    private String investmentPortfolioName;

    @NotNull
    private double totalInvested;

    @NotNull
    private double amtAvailable;
}
