package com.pismo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.account.dto.request.AccountRequest;
import com.pismo.account.dto.request.LoginRequest;
import com.pismo.account.dto.request.RegisterRequest;
import com.pismo.account.dto.request.TransactionRequest;
import com.pismo.account.dto.response.AccountResponse;
import com.pismo.account.dto.response.JwtResponse;
import com.pismo.account.dto.response.MessageResponse;
import com.pismo.account.dto.response.TransactionResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Full Integration Tests - Complete API Flow")
class FullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;
    private static Long accountId;

    @Test
    @Order(1)
    @DisplayName("1. Should register a new user successfully")
    void testRegisterUser() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("integrationtest");
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MessageResponse response = objectMapper.readValue(responseBody, MessageResponse.class);
        assertThat(response.getMessage()).isEqualTo("User registered successfully");
    }

    @Test
    @Order(2)
    @DisplayName("2. Should not allow duplicate username registration")
    void testRegisterDuplicateUsername() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("integrationtest"); // Same as before
        registerRequest.setEmail("different@test.com");
        registerRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("3. Should login with registered user and receive JWT token")
    void testLogin() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("integrationtest");
        loginRequest.setPassword("password123");

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("integrationtest"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andReturn();

        // Extract JWT token for subsequent requests
        String responseBody = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseBody, JwtResponse.class);
        jwtToken = jwtResponse.getToken();

        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken).startsWith("eyJ"); // JWT tokens start with eyJ
    }

    @Test
    @Order(4)
    @DisplayName("4. Should return 403 for unauthorized access")
    void testUnauthorizedAccess() throws Exception {
        // Arrange
        AccountRequest accountRequest = new AccountRequest("12345678900");

        // Act & Assert
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isForbidden()); // Changed from isUnauthorized to isForbidden
    }

    @Test
    @Order(5)
    @DisplayName("5. Should create account with valid JWT token")
    void testCreateAccount() throws Exception {
        // Arrange
        AccountRequest accountRequest = new AccountRequest("12345678900");

        // Act
        MvcResult result = mockMvc.perform(post("/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").exists())
                .andExpect(jsonPath("$.document_number").value("12345678900"))
                .andReturn();

        // Extract account ID for subsequent requests
        String responseBody = result.getResponse().getContentAsString();
        AccountResponse accountResponse = objectMapper.readValue(responseBody, AccountResponse.class);
        accountId = accountResponse.getAccountId();

        assertThat(accountId).isNotNull();
    }

    @Test
    @Order(6)
    @DisplayName("6. Should not allow duplicate account creation")
    void testCreateDuplicateAccount() throws Exception {
        // Arrange
        AccountRequest accountRequest = new AccountRequest("12345678900"); // Same document number

        // Act & Assert
        mockMvc.perform(post("/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(7)
    @DisplayName("7. Should retrieve account by ID")
    void testGetAccount() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/accounts/" + accountId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(accountId))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    @Order(8)
    @DisplayName("8. Should return 404 for non-existent account")
    void testGetNonExistentAccount() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/accounts/99999")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("9. Should create transaction with negative amount for purchase")
    void testCreatePurchaseTransaction() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setOperationTypeId(1L); // PURCHASE
        transactionRequest.setAmount(BigDecimal.valueOf(100.50));

        // Act
        MvcResult result = mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").exists())
                .andExpect(jsonPath("$.account_id").value(accountId))
                .andExpect(jsonPath("$.operation_type_id").value(1))
                .andExpect(jsonPath("$.amount").value(-100.50)) // Should be negative
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        TransactionResponse response = objectMapper.readValue(responseBody, TransactionResponse.class);
        assertThat(response.getAmount()).isNegative();
    }

    @Test
    @Order(10)
    @DisplayName("10. Should create transaction with positive amount for payment")
    void testCreatePaymentTransaction() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setOperationTypeId(4L); // PAYMENT
        transactionRequest.setAmount(BigDecimal.valueOf(50.25));

        // Act
        MvcResult result = mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").exists())
                .andExpect(jsonPath("$.account_id").value(accountId))
                .andExpect(jsonPath("$.operation_type_id").value(4))
                .andExpect(jsonPath("$.amount").value(50.25)) // Should be positive
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        TransactionResponse response = objectMapper.readValue(responseBody, TransactionResponse.class);
        assertThat(response.getAmount()).isPositive();
    }

    @Test
    @Order(11)
    @DisplayName("11. Should reject transaction for non-existent account")
    void testCreateTransactionForNonExistentAccount() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(99999L); // Non-existent
        transactionRequest.setOperationTypeId(1L);
        transactionRequest.setAmount(BigDecimal.valueOf(100.00));

        // Act & Assert
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    @DisplayName("12. Should handle withdrawal transaction correctly")
    void testCreateWithdrawalTransaction() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setOperationTypeId(3L); // WITHDRAWAL
        transactionRequest.setAmount(BigDecimal.valueOf(75.00));

        // Act & Assert
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(-75.00)); // Should be negative
    }

    @Test
    @Order(13)
    @DisplayName("13. Should handle installment purchase transaction correctly")
    void testCreateInstallmentPurchaseTransaction() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setOperationTypeId(2L); // INSTALLMENT PURCHASE
        transactionRequest.setAmount(BigDecimal.valueOf(200.00));

        // Act & Assert
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(-200.00)); // Should be negative
    }

    @Test
    @Order(14)
    @DisplayName("14. Should reject invalid transaction with negative amount input for payment")
    void testRejectNegativeAmountForPayment() throws Exception {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setOperationTypeId(4L); // PAYMENT
        transactionRequest.setAmount(BigDecimal.valueOf(-50.00)); // Invalid: negative amount

        // Act & Assert
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    @DisplayName("15. Should check health endpoint is public")
    void testHealthEndpointPublic() throws Exception {
        // Act & Assert (no JWT token needed)
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
