package com.djokic.userserviceff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException userNotFoundException){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of("status", HttpStatus.NOT_FOUND.value(),
                                "error", "Not Found",
                                "message", userNotFoundException.getMessage()
                        )
                );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException emailAlreadyExistsException){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of("status", HttpStatus.CONFLICT.value(),
                                "error", "Conflict",
                                "message", emailAlreadyExistsException.getMessage()
                        )
                );
    }

    @ExceptionHandler(PasswordLengthException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordLengthException(PasswordLengthException passwordLengthException){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("status", HttpStatus.BAD_REQUEST.value(),
                                "error", "Bad Request",
                                "message", passwordLengthException.getMessage()
                        )
                );
    }

    @ExceptionHandler(EmailNotProvidedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotProvidedException(EmailNotProvidedException emailNotProvidedException){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("status", HttpStatus.BAD_REQUEST.value(),
                                "error", "Bad Request",
                                "message", emailNotProvidedException.getMessage()
                        )
                );
    }

    @ExceptionHandler(PasswordNotProvidedException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordNotProvidedException(PasswordNotProvidedException passwordNotProvidedException){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("status", HttpStatus.BAD_REQUEST.value(),
                                "error", "Bad Request",
                                "message", passwordNotProvidedException.getMessage()
                        )
                );
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleWrongCredentialsException(WrongCredentialsException wrongCredentialsException){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        Map.of("status", HttpStatus.UNAUTHORIZED.value(),
                                "error", "Unauthorized",
                                "message", wrongCredentialsException.getMessage()
                        )
                );
    }
}