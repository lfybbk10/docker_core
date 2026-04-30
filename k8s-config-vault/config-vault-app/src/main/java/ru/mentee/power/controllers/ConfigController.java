package ru.mentee.power.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.config.ConfigurationService;

@RestController
public class ConfigController {

    private final ConfigurationService configurationService;

    public ConfigController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/app-config")
    public String appConfig() {
        return configurationService.loadConfiguration().toString();
    }

    @GetMapping("/secrets")
    public String secrets() {
        return configurationService.loadSecrets().toString();
    }
}
