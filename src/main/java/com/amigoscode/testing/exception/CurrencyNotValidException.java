package com.amigoscode.testing.exception;

public class CurrencyNotValidException extends RuntimeException{

    String msg;

    public CurrencyNotValidException(String msg) {
        super(msg);
    }
}
