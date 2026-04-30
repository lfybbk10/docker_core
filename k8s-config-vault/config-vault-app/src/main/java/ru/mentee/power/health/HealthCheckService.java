package ru.mentee.power.health;

import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import ru.mentee.power.vault.VaultIntegration;

import java.util.Map;

@Service
public class HealthCheckService {
    private final VaultTemplate vaultTemplate;

    public HealthCheckService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    public boolean isVaultAvailable() {
        try{
            vaultTemplate.opsForSys().health();
            return true;
        } catch (Exception e) {
            System.out.println("Vault is NOT available "+e.getMessage());
            return false;
        }
    }

    public long getCredentialsSeconds(){
        VaultResponse response = vaultTemplate.read("database/creds/db-app-role");

        if (response == null) {
            throw new IllegalStateException("No response from Vault");
        }

        return response.getLeaseDuration();
    }
}
