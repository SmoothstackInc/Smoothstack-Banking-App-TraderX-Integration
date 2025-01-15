package com.smoothstack.investment_orchestrator.dto;


import java.util.List;

public record TimeSeriesDTO(String symbol, List<TimeSeriesData> data) {
}