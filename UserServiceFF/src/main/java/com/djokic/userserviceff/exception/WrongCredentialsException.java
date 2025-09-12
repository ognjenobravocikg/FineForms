package com.djokic.userserviceff.exception;

public class WrongCredentialsException extends RuntimeException{
    public WrongCredentialsException() {
        super("Invalid email or password!");
    }
}
