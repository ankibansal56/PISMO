package com.pismo.account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Account ID is required")
    @JsonProperty("account_id")
    private Long accountId;

    @NotNull(message = "Operation type ID is required")
    @JsonProperty("operation_type_id")
    private Long operationTypeId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;
}
