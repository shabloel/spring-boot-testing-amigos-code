package com.amigoscode.testing.exception;

import org.springframework.data.repository.query.Param;

public class CustomerAlreadyExistsException extends RuntimeException{

    private String message;

    public CustomerAlreadyExistsException(String message) {

        super(message);
    }
}
