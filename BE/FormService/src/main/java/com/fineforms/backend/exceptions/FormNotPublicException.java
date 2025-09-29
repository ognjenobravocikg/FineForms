package com.fineforms.backend.exceptions;

public class FormNotPublicException extends RuntimeException {
    public FormNotPublicException(String message) {
        super(message);
    }

    public FormNotPublicException(Long id) {
        super("Form with id " + id + " requires authentication and is not publicly accessible");
    }
}