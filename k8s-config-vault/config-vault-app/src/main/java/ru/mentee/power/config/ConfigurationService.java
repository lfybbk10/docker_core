package ru.mentee.power.config;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;

@Service
public class ConfigurationService {
    public Properties loadConfiguration() {
        // Подсказка: /config/application.properties
        Properties props = new Properties();
        try(InputStream inputStream = Files.newInputStream(Path.of("/config/application.properties"))) {
            props.load(inputStream);
        } catch (IOException e) {
            props.setProperty("app.timeout", "5000");
            props.setProperty("feature.enabled", "false");
        }
        return props;
    }

    // TODO: Реализовать загрузку из Secrets
    public Credentials loadSecrets() {
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        return new Credentials(username, password);
    }

    public void watchConfigChanges() {
        Path configDir = Path.of("/config");

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            configDir.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals("application.properties")) {
                        Properties updated = loadConfiguration();
                        // тут обновляешь текущий config в памяти

                    }
                }

                key.reset();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}