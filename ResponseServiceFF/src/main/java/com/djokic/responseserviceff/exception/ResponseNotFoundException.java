package com.djokic.responseserviceff.exception;

public class ResponseNotFoundException extends CustomException {
    public ResponseNotFoundException(Long id) {
        super("Response not found with id: " + id);
    }
}