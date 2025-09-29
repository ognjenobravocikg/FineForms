package com.djokic.userserviceff.exception;

public class PasswordLengthException extends RuntimeException{
    public PasswordLengthException() {
        super("Provided password is shorter than required. Passwords should be 8 characters long !");
    }
}
