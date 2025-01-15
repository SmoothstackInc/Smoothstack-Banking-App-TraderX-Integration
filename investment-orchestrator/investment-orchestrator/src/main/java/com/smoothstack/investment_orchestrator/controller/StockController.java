package com.smoothstack.investment_orchestrator.controller;

import com.smoothstack.investment_orchestrator.dto.AdviceAggregateModelDTO;
import com.smoothstack.investment_orchestrator.dto.MetaDataDTO;
import com.smoothstack.investment_orchestrator.dto.TimeSeriesDTO;
import com.smoothstack.investment_orchestrator.serviceImpl.LiveDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class StockController {
    @Autowired
    private final LiveDataService lds;

    @Operation(summary = "Get timeseries data for a stock", description = "Get timeseries data for a stock")
    @GetMapping("/history/{symbol}")
    public TimeSeriesDTO getStockHistory(
            @Parameter(description = "Stock symbol, e.g., GOOGL or AAPL", example = "GOOGL")
            @PathVariable String symbol) {
        return lds.getStockHistory(symbol);
    }

    @Operation(summary = "Gets the meta-data for a stock", description = "Gets the meta-data for a stock")
    @GetMapping("/meta-data/{symbol}")
    public MetaDataDTO getMetaData(
            @Parameter(description = "Stock symbol, e.g., GOOGL or AAPL", example = "GOOGL")
            @PathVariable String symbol) {
        return lds.getMetaData(symbol);
    }

    @Operation(summary = "Aggregates information for LLM", description = "Aggregates information for LLM")
    @GetMapping("/aggregated/{symbol}/{investmentPortfolioId}")
    public ResponseEntity<AdviceAggregateModelDTO> getAggregatedStockData(
            @PathVariable String symbol,
            @PathVariable Integer investmentPortfolioId) {
        AdviceAggregateModelDTO aggregatedData = lds.getAdvice(investmentPortfolioId, symbol);
        return ResponseEntity.ok(aggregatedData);
    }
}