package ru.mentee.power.health;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import java.util.Random;

public class DatabaseHealthIndicator implements HealthIndicator {
    private Random random = new Random();

    private boolean isDatabaseHealthy = true;

    @Override
    public Health health() {
        boolean databaseHealth = checkDatabaseConnection();
        if (!databaseHealth) {
            return Health.down()
                    .withDetail("database_health", false)
                    .withDetail("message", "Database connection failed!")
                    .build();
        }

        return Health.up()
                .withDetail("database_health", true)
                .withDetail("response_time", getDatabaseResponseTime())
                .withDetail("message", "Database connection successful!")
                .build();
    }

    private boolean checkDatabaseConnection() {
        // Симуляция проверки БД
        return isDatabaseHealthy;
    }

    public void crashDatabase(){
        isDatabaseHealthy = false;
    }

    private long getDatabaseResponseTime() {
        return random.nextInt(5);
    }
}
