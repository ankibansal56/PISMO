package com.pismo.account.service;

import com.pismo.account.domain.entity.Account;
import com.pismo.account.dto.request.AccountRequest;
import com.pismo.account.dto.response.AccountResponse;
import com.pismo.account.exception.DuplicateResourceException;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private AccountRequest accountRequest;
    private Account account;

    @BeforeEach
    void setUp() {
        accountRequest = new AccountRequest("12345678900");
        account = new Account(1L, "12345678900");
    }

    @Test
    @DisplayName("Should create account successfully")
    void createAccount_Success() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.createAccount(accountRequest);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when document number already exists")
    void createAccount_DuplicateDocumentNumber() {
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate document number"));

        assertThrows(DuplicateResourceException.class, 
                () -> accountService.createAccount(accountRequest));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should get account successfully")
    void getAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccount(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("12345678900", response.getDocumentNumber());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void getAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> accountService.getAccount(1L));
    }
}
