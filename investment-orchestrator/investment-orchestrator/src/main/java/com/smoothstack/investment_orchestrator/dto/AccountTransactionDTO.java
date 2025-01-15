package com.smoothstack.investment_orchestrator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountTransactionDTO {

    private Integer accountTransactionId;

    @NotNull
    private Integer accountId;

    @NotNull
    private Integer accountUserId;

    @NotNull
    private double fundsMoved;

}
