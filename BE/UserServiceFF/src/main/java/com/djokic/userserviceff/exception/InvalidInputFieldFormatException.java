package com.djokic.userserviceff.exception;

public class InvalidInputFieldFormatException extends RuntimeException {
    public InvalidInputFieldFormatException(String fieldName) {
        super("Invalid input field " + fieldName);
    }
}
