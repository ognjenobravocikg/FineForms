package com.djokic.userserviceff.exception;

public class FirstNameNotProvidedException extends RuntimeException {
    public FirstNameNotProvidedException() {
        super("First name must be provided !");
    }
}
