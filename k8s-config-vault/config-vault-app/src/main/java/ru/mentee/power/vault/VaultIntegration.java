package ru.mentee.power.vault;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.lease.event.SecretLeaseRotatedEvent;
import org.springframework.vault.support.VaultResponse;
import ru.mentee.power.config.Credentials;

import java.util.Map;

@Service
public class VaultIntegration {
    private final VaultTemplate vaultTemplate;

    public VaultIntegration(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public Credentials getDynamicCredentials() {
        VaultResponse response = vaultTemplate.read("database/creds/db-app-role");

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("No credentials from Vault");
        }

        Map<String, Object> data = response.getData();

        return new Credentials(
                data.get("username").toString(),
                data.get("password").toString()
        );
    }

    @EventListener
    public void handleLeaseRotation(SecretLeaseRotatedEvent event) {
        System.out.println("Vault lease rotated: " + event.getSource());

        Credentials credentials = getDynamicCredentials();

        System.out.println("New database credentials received. username= " + credentials.username());
    }
}
