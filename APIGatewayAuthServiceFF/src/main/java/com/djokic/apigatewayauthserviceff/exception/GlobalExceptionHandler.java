package com.djokic.apigatewayauthserviceff.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignClientException(FeignException e) {
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, Object> body;
        if (e.responseBody().isPresent()) {
            try {
                byte[] bytes = e.responseBody().get().array();
                String json = new String(bytes, StandardCharsets.UTF_8);
                body = objectMapper.readValue(json, Map.class);
            } catch (Exception ex) {
                body = Map.of(
                        "message", "An error occurred while processing the request.",
                        "status", status.value(),
                        "error", status.getReasonPhrase()
                );
            }
        } else {
            body = Map.of(
                    "message", "An error occurred while processing the request. No response body available.",
                    "status", status.value(),
                    "error", status.getReasonPhrase()
            );
        }

        return ResponseEntity.status(status).body(body);
    }

}