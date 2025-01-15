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
public class AccountDTO {

    private Integer accountId;

    @NotBlank
    private String accountName;

    @NotBlank
    private String accountDescription;

    @NotNull
    private double totalInvested;

    @NotNull
    private double amtAvailable;
}
