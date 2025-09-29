package com.djokic.userserviceff.exception;

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("Provided email is not valid !");
    }
}
