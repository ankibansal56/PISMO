package com.pismo.account.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "document_number", nullable = false, unique = true)
    private String documentNumber;

    @Column(name = "available_credit_limit", nullable = false)
    private BigDecimal availableCreditLimit = new BigDecimal(1000);

    @Column(name="balance", nullable = false)
    private BigDecimal balance = new BigDecimal(-1000);

    public Boolean hasSufficientbalance(BigDecimal amount){
        if(amount.compareTo(this.balance) < 0){
            return false;
        }
        return true;
    }
}
