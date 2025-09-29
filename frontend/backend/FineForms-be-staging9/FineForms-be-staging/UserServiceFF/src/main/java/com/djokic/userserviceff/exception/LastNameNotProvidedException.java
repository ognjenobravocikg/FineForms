package com.djokic.userserviceff.exception;

public class LastNameNotProvidedException extends RuntimeException {
    public LastNameNotProvidedException() {
      super("Last name must be provided !");
    }
}
