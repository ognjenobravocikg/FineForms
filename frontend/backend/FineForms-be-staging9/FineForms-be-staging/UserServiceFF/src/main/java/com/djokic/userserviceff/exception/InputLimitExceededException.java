package com.djokic.userserviceff.exception;

public class InputLimitExceededException extends RuntimeException {
    public InputLimitExceededException(String fieldName) {
        super("Input limit exceeded for field " + fieldName);
    }
}
