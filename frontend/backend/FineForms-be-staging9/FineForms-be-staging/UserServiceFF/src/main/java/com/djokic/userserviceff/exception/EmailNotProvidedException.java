package com.djokic.userserviceff.exception;

public class EmailNotProvidedException extends RuntimeException{
    public EmailNotProvidedException() {
        super("Email must be provided !");
    }
}
