package com.pismo.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.account.dto.request.AccountRequest;
import com.pismo.account.dto.response.AccountResponse;
import com.pismo.account.exception.DuplicateResourceException;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.security.JwtAuthenticationFilter;
import com.pismo.account.security.JwtTokenProvider;
import com.pismo.account.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /accounts - Should create account successfully")
    void createAccount_Success() throws Exception {
        AccountRequest request = new AccountRequest("12345678900");
        AccountResponse response = new AccountResponse(1L, "12345678900");

        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    @DisplayName("POST /accounts - Should return 400 for invalid request")
    void createAccount_InvalidRequest() throws Exception {
        AccountRequest request = new AccountRequest(""); // Invalid empty document

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /accounts - Should return 409 for duplicate account")
    void createAccount_Duplicate() throws Exception {
        AccountRequest request = new AccountRequest("12345678900");

        when(accountService.createAccount(any(AccountRequest.class)))
                .thenThrow(new DuplicateResourceException("Account already exists"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /accounts/{id} - Should get account successfully")
    void getAccount_Success() throws Exception {
        AccountResponse response = new AccountResponse(1L, "12345678900");

        when(accountService.getAccount(1L)).thenReturn(response);

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    @DisplayName("GET /accounts/{id} - Should return 404 for non-existent account")
    void getAccount_NotFound() throws Exception {
        when(accountService.getAccount(999L))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/accounts/999"))
                .andExpect(status().isNotFound());
    }
}
