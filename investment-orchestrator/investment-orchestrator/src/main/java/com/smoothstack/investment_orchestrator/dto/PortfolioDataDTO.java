package com.smoothstack.investment_orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Contains a list of positions in a portfolio")
public record PortfolioDataDTO(
        @Schema(description = "List of stock positions in the portfolio")
        List<SimplePositionDTO> positions
) {
}