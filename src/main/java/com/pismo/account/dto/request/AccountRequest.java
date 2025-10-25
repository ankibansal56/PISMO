package com.pismo.account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Document number is required")
    @Size(min = 11, max = 14, message = "Document number must be between 11 and 14 characters")
    @JsonProperty("document_number")
    private String documentNumber;
}
