package com.smoothstack.investment_orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a simple stock position in the portfolio")
public record SimplePositionDTO(
        @Schema(description = "Stock ticker symbol", example = "GOOGL")
        String ticker,

        @Schema(description = "Total quantity of shares", example = "50")
        int totalQuantity
) {
}