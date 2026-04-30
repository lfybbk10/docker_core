package ru.mentee.power.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.health.HealthCheckService;

import java.util.Map;

@RestController
public class HealthController {

    private final HealthCheckService health;

    public HealthController(HealthCheckService health) {
        this.health = health;
    }

    @GetMapping("/health/vault")
    public Map<String, Object> vaultHealth() {
        return Map.of(
                "vaultAvailable", health.isVaultAvailable(),
                "ttlSeconds", health.getCredentialsSeconds()
        );
    }
}