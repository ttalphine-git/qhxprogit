package com.qhxpro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "QHX Pro Application is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\"}";
    }

    @GetMapping("/api/version")
    public String version() {
        return "{\"version\": \"1.0.0\", \"name\": \"QHX Pro\"}";
    }
}
