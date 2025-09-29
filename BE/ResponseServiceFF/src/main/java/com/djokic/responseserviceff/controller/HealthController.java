package com.djokic.responseserviceff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Response Service is running!";
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Response Service - use /response endpoints";
    }
}