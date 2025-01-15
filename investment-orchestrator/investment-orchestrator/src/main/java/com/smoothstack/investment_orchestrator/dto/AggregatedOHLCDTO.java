package com.smoothstack.investment_orchestrator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AggregatedOHLCDTO(LocalDate performanceStartDate,
                                LocalDate performanceEndDate,
                                BigDecimal open,
                                BigDecimal high,
                                BigDecimal low,
                                BigDecimal close,
                                Long aggregateVolume) {
}