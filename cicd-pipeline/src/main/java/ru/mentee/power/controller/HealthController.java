package ru.mentee.power.controller;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.mentee.power.CicdApplication.AppInfo;
import ru.mentee.power.CicdApplication.HealthStatus;

@RestController
public class HealthController {

    private final String version;
    private final String deployTime;

    public HealthController(
        @Value("${APP_VERSION:${app.version:dev}}") String version,
        @Value("${DEPLOY_TIME:}") String deployTime
    ) {
        this.version = version;
        this.deployTime = deployTime == null || deployTime.isBlank()
            ? Instant.now().toString()
            : deployTime;
    }

    @GetMapping("/")
    public AppInfo getInfo() {
        return new AppInfo(version, deployTime, hostname());
    }

    @GetMapping("/health")
    public HealthStatus health() {
        return new HealthStatus("UP", ManagementFactory.getRuntimeMXBean().getUptime());
    }

    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exception) {
            return "unknown-host";
        }
    }
}

