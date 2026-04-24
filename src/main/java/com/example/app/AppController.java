package com.example.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class AppController {

    @Value("${spring.application.name:java-maven-app}")
    private String appName;

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "app", appName,
            "status", "running",
            "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
            "app", appName,
            "java", System.getProperty("java.version"),
            "os", System.getProperty("os.name")
        );
    }
}
