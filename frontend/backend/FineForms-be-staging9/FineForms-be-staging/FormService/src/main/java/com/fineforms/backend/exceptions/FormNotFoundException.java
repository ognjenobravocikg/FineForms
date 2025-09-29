package com.fineforms.backend.exceptions;

public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(String message) {
        super(message);
    }

    public FormNotFoundException(Long id) {
        super("Form not found with id: " + id);
    }
}
