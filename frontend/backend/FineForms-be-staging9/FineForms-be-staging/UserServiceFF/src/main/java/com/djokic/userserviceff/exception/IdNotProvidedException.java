package com.djokic.userserviceff.exception;

public class IdNotProvidedException extends RuntimeException {
    public IdNotProvidedException() {
        super("Id must be provided !");
    }
}
