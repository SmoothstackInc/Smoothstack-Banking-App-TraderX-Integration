package com.smoothstack.investment_orchestrator.dto;

public record AdviceAggregateModelDTO(AggregatedStockDataDTO stockData,
                                      PortfolioAggregateDTO portfolioData) {
}