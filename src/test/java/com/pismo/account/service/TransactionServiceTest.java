package com.pismo.account.service;

import com.pismo.account.domain.entity.Account;
import com.pismo.account.domain.entity.Transaction;
import com.pismo.account.dto.request.TransactionRequest;
import com.pismo.account.dto.response.TransactionResponse;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        account = new Account(1L, "12345678900");
    }

    @Test
    @DisplayName("Should create purchase transaction with negative amount")
    void createTransaction_Purchase_NegativeAmount() {
        transactionRequest = new TransactionRequest(1L, 1L, new BigDecimal("50.00"));
        Transaction savedTransaction = new Transaction(
                1L, account, 1L, new BigDecimal("-50.00"), LocalDateTime.now());

        when(accountService.findAccountById(1L)).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.createTransaction(transactionRequest);

        assertNotNull(response);
        assertEquals(1L, response.getTransactionId());
        assertEquals(1L, response.getAccountId());
        assertEquals(1L, response.getOperationTypeId());
        assertEquals(new BigDecimal("-50.00"), response.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create payment transaction with positive amount")
    void createTransaction_Payment_PositiveAmount() {
        transactionRequest = new TransactionRequest(1L, 4L, new BigDecimal("60.00"));
        Transaction savedTransaction = new Transaction(
                1L, account, 4L, new BigDecimal("60.00"), LocalDateTime.now());

        when(accountService.findAccountById(1L)).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.createTransaction(transactionRequest);

        assertNotNull(response);
        assertEquals(new BigDecimal("60.00"), response.getAmount());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void createTransaction_AccountNotFound() {
        transactionRequest = new TransactionRequest(999L, 1L, new BigDecimal("50.00"));

        when(accountService.findAccountById(999L))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(transactionRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when operation type not found")
    void createTransaction_OperationTypeNotFound() {
        transactionRequest = new TransactionRequest(1L, 999L, new BigDecimal("50.00"));

        when(accountService.findAccountById(1L)).thenReturn(account);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(transactionRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
