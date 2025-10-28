package com.pismo.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.account.dto.request.TransactionRequest;
import com.pismo.account.dto.response.TransactionResponse;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.security.JwtAuthenticationFilter;
import com.pismo.account.security.JwtTokenProvider;
import com.pismo.account.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /transactions - Should create transaction successfully")
    void createTransaction_Success() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 4L, new BigDecimal("123.45"));
        TransactionResponse response = new TransactionResponse(1L, 1L, 4L, new BigDecimal("123.45"));

        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").value(1))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.operation_type_id").value(4))
                .andExpect(jsonPath("$.amount").value(123.45));
    }

    @Test
    @DisplayName("POST /transactions - Should return 400 for invalid request")
    void createTransaction_InvalidRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(null, null, null);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /transactions - Should return 404 for non-existent account")
    void createTransaction_AccountNotFound() throws Exception {
        TransactionRequest request = new TransactionRequest(999L, 1L, new BigDecimal("50.00"));

        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /transactions - Should return 404 for invalid operation type")
    void createTransaction_OperationTypeNotFound() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 999L, new BigDecimal("50.00"));

        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Operation type not found"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
