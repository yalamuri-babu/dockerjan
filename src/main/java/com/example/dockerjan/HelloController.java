package com.example.dockerjan;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Spring Boot Docker App is running";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\"}";
    }
}
