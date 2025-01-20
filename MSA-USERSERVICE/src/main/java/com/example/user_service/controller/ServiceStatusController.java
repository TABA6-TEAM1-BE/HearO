package com.example.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@Slf4j
public class ServiceStatusController implements HealthIndicator {

    private boolean isDown = false;

    @Override
    public Health health() {
        if (isDown) {
            return Health.down().withDetail("status", "Service manually set to DOWN").build();
        }
        return Health.up().build();
    }

    @PostMapping("/down")
    public String setServiceDown() {
        log.info("로그 찍힘");
        isDown = true;
        return "Service is now DOWN";
    }

    @PostMapping("/up")
    public String setServiceUp() {
        isDown = false;
        return "Service is now UP";
    }
}
