package com.amigoscode.testing.exception;

public class CustomerNotFoundException extends RuntimeException{

    String msg;

    public CustomerNotFoundException(String msg) {
        super(msg);
    }
}
