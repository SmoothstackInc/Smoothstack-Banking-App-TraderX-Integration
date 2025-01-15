package com.smoothstack.investment_orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PositionTransactionDTO {

    private Integer positionTransactionId;

    @NotNull
    private Integer positionId;

    @NotNull
    private double lockedPrice;

    @NotNull
    private Integer quantity;

    @NotBlank
    private String side;
}
