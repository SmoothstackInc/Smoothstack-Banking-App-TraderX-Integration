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
public class AccountUserDTO {

    private Integer accountUserId;

    @NotNull
    private Integer userId;

    @NotBlank
    private String userName;

}
