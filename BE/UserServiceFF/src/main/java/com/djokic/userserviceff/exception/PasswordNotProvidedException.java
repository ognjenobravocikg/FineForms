package com.djokic.userserviceff.exception;

public class PasswordNotProvidedException extends RuntimeException {
    public PasswordNotProvidedException() {
        super("Password must be provided !");
    }
}
