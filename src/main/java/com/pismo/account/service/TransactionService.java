package com.pismo.account.service;

import com.pismo.account.domain.entity.Account;
import com.pismo.account.domain.entity.Transaction;
import com.pismo.account.domain.enums.OperationTypeEnum;
import com.pismo.account.dto.request.TransactionRequest;
import com.pismo.account.dto.response.TransactionResponse;
import com.pismo.account.exception.ResourceNotFoundException;
import com.pismo.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction for account ID: {}, operation type: {}", 
                request.getAccountId(), request.getOperationTypeId());

        // Validate account exists
        Account account = accountService.findAccountById(request.getAccountId());

        // Validate operation type is valid
        OperationTypeEnum operationTypeEnum = OperationTypeEnum.fromId(request.getOperationTypeId());
        if (operationTypeEnum == null) {
            throw new ResourceNotFoundException("Operation type not found with ID: " + request.getOperationTypeId());
        }

        // Apply sign based on operation type (debt transactions are negative)
        BigDecimal amount = calculateAmount(request.getAmount(), request.getOperationTypeId());

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperationTypeId(request.getOperationTypeId());
        transaction.setAmount(amount);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getTransactionId());

        return new TransactionResponse(
                savedTransaction.getTransactionId(),
                savedTransaction.getAccount().getAccountId(),
                savedTransaction.getOperationTypeId(),
                savedTransaction.getAmount()
        );
    }

    private BigDecimal calculateAmount(BigDecimal amount, Long operationTypeId) {
        OperationTypeEnum operationTypeEnum = OperationTypeEnum.fromId(operationTypeId);
        
        if (operationTypeEnum == null) {
            throw new ResourceNotFoundException("Invalid operation type ID: " + operationTypeId);
        }

        // For debt transactions (purchase, installment purchase, withdrawal), amount should be negative
        if (operationTypeEnum.isNegative()) {
            return amount.abs().negate();
        }
        
        // For credit transactions (payment), amount should be positive
        return amount.abs();
    }
}
