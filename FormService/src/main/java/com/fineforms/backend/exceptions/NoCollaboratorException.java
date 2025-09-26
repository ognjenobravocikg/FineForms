package com.fineforms.backend.exceptions;

public class NoCollaboratorException extends RuntimeException {
    public NoCollaboratorException(Long formId) {
        super("No collaborators found for form with id " + formId);
    }
}
