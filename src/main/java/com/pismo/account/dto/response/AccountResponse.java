package com.pismo.account.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("document_number")
    private String documentNumber;
}
