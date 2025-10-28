package com.pismo.account.service;

import com.pismo.account.domain.entity.Account;
import com.pismo.account.dto.request.AccountRequest;
import com.pismo.account.dto.response.AccountResponse;
import com.pismo.account.exception.DuplicateResourceException;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.info("Creating account for document number: {}", request.getDocumentNumber());
        
        try {
            Account account = new Account();
            account.setDocumentNumber(request.getDocumentNumber());
            
            Account savedAccount = accountRepository.save(account);
            log.info("Account created successfully with ID: {}", savedAccount.getAccountId());
            
            return new AccountResponse(savedAccount.getAccountId(), savedAccount.getDocumentNumber());
            
        } catch (DataIntegrityViolationException e) {
            // Database unique constraint caught duplicate (race condition occurred)
            log.warn("Duplicate account creation attempted for document number: {}", request.getDocumentNumber());
            throw new DuplicateResourceException(
                    "Account with document number " + request.getDocumentNumber() + " already exists");
        }
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        log.info("Retrieving account with ID: {}", accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        
        return new AccountResponse(account.getAccountId(), account.getDocumentNumber());
    }

    @Transactional(readOnly = true)
    public Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
    }
}
