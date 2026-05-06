package ru.mentee.power.health;


import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    private static final double MAX_USED_MEMORY_PERCENT = 90.0;

    @Override
    public Health health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        long used = heapMemoryUsage.getUsed();
        long max = heapMemoryUsage.getMax();

        double usedPercent = ((double) used / max) * 100;

        if (usedPercent >= MAX_USED_MEMORY_PERCENT) {
            return Health.down()
                    .withDetail("message", "JVM heap memory usage is too high")
                    .withDetail("used_bytes", used)
                    .withDetail("max_bytes", max)
                    .withDetail("used_percent", usedPercent)
                    .build();
        }

        return Health.up()
                .withDetail("message", "JVM heap memory usage is normal")
                .withDetail("used_bytes", used)
                .withDetail("max_bytes", max)
                .withDetail("used_percent", usedPercent)
                .build();
    }
}