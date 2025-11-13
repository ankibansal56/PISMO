package com.pismo.account.exception;

public class InsufficientBalance extends RuntimeException {

    public InsufficientBalance(String message) {
        super(message);
    }
}
