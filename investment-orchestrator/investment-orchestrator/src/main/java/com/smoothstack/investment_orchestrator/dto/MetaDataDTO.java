package com.smoothstack.investment_orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MetaDataDTO(
        @Schema(description = "Stock ticker symbol", example = "GOOGL")
        String symbol,

        @Schema(description = "Security name", example = "Alphabet (Class A)")
        String security,

        @Schema(description = "Link to SEC filings", example = "reports")
        String secFilings,

        @Schema(description = "GICS Sector", example = "Communication Services")
        String gicsSector,

        @Schema(description = "GICS Sub-Industry", example = "Interactive Media & Services")
        String gicsSubIndustry,

        @Schema(description = "Headquarters Location", example = "Mountain View, California")
        String headquartersLocation,

        @Schema(description = "Date the stock was first added", example = "03/04/2014")
        String dateFirstAdded,

        @Schema(description = "Central Index Key (CIK) for SEC filings", example = "1652044")
        String cik,

        @Schema(description = "Year the company was founded", example = "1998")
        String founded
) {
}