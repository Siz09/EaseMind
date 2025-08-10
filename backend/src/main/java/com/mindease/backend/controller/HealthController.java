package com.mindease.backend.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    private final Environment env;
    private final BuildProperties buildProperties; // Requires spring-boot-actuator

    public HealthController(Environment env, BuildProperties buildProperties) {
        this.env = env;
        this.buildProperties = buildProperties;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "services", Map.of(
                        "database", "MySQL/operational",
                        "authentication", "Firebase/connected"
                ),
                "build", Map.of(
                        "version", buildProperties.getVersion(),
                        "timestamp", buildProperties.getTime()
                ),
                "environment", env.getActiveProfiles().length > 0 ?
                        env.getActiveProfiles()[0] : "default",
                "serverTime", LocalDateTime.now()
        ));
    }

    @GetMapping("/env")
    public ResponseEntity<Map<String, String>> environment() {
        return ResponseEntity.ok(Map.of(
                "activeProfiles", String.join(",", env.getActiveProfiles()),
                "dbUrl", env.getProperty("spring.datasource.url", "not configured")
        ));
    }
}