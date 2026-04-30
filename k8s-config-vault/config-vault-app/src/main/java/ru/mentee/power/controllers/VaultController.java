package ru.mentee.power.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.vault.VaultIntegration;

@RestController
public class VaultController {

    private final VaultIntegration vaultIntegration;

    public VaultController(VaultIntegration vaultIntegration) {
        this.vaultIntegration = vaultIntegration;
    }

    @GetMapping("/db-creds")
    public String dbCreds() {
        return vaultIntegration.getDynamicCredentials().toString();
    }
}
