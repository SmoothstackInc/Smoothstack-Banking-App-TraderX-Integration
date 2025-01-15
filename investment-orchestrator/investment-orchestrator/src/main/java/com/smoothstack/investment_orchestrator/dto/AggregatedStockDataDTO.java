package com.smoothstack.investment_orchestrator.dto;

public record AggregatedStockDataDTO(String symbol,
                                     String sentiment,
                                     AggregatedOHLCDTO fiveDaySummary,
                                     AggregatedOHLCDTO tenDaySummary,
                                     AggregatedOHLCDTO fifteenDaySummary,
                                     AggregatedOHLCDTO thirtyDaySummary) {
}