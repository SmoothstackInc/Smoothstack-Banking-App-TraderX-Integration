package com.smoothstack.investment_orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeRequestDTO {

    private Integer tradeRequestId;

    @NotNull
    private Integer investmentPortfolioId;

    @NotBlank
//    @Pattern(regexp = "^[A-Z]{4}$", message = "Invalid ticker format. Use XXXX.")
    private String ticker;

    @NotNull
    private String securityName;

    @NotNull
    private double price;

    @NotNull
    private Integer quantity;

    @NotBlank
    private String side;
}
