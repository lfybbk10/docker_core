package ru.mentee.power;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CicdApplication {

    public static void main(String[] args) {
        SpringApplication.run(CicdApplication.class, args);
    }

    public record AppInfo(String version, String deployTime, String hostname) {
    }

    public record HealthStatus(String status, long uptime) {
    }
}

