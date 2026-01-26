package com.example.dockerjan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${spring.application.name:Spring Boot Docker App}")
    private String appName;

    @GetMapping("/")
    public String hello() {
        return appName + " is running";
    }
}
