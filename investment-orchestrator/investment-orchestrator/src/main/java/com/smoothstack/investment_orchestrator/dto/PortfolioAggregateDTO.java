package com.smoothstack.investment_orchestrator.dto;

import java.util.List;

public record PortfolioAggregateDTO(double totalInvestedAmount, double availableFunds, List<PosAggDTO> positions) {
}