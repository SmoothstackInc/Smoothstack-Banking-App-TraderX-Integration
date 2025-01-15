package com.smoothstack.investment_orchestrator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvestmentPortfolioTransactionDTO {

    private Integer investmentPortfolioTransactionId;

    @NotNull
    private Integer investmentPortfolioId;

    @NotNull
    private Integer investorId;

    @NotNull
    private double fundsMoved;
}
