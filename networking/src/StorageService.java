package ru.mentee.power.storage;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StorageService {
    private final PayloadStore payloadStore;

    public StorageService(PayloadStore payloadStore) {
        this.payloadStore = payloadStore;
    }

    public static void main(String[] args) {
        SpringApplication.run(StorageService.class, args);
    }

    @GetMapping(path = "/storage/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> storageData() {
        try {
            return ResponseEntity.ok(this.payloadStore.readPayload());
        } catch (IllegalStateException ex) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "storage");
            response.put("status", "ERROR");
            response.put("timestamp", Instant.now().toString());
            response.put("message", "Cannot read payload from storage");
            response.put("details", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = this.payloadStore.health();
        HttpStatus status = "UP".equals(health.get("status"))
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }

    @Service
    static class PayloadStore {
        private static final String CACHE_KEY = "storage:payload";
        private static final String DEFAULT_PAYLOAD = "payload from isolated storage";

        private final StringRedisTemplate redisTemplate;
        private final Path storagePath;

        PayloadStore(
                StringRedisTemplate redisTemplate,
                @Value("${storage.path:/data/storage-message.txt}") String storagePath) {
            this.redisTemplate = redisTemplate;
            this.storagePath = Path.of(storagePath);
        }

        @PostConstruct
        void initialize() throws IOException {
            Path parent = this.storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(this.storagePath)) {
                Files.writeString(
                        this.storagePath,
                        DEFAULT_PAYLOAD,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        }

        Map<String, Object> readPayload() {
            boolean redisAvailable = false;
            String redisError = null;
            String cachedPayload = null;

            try {
                cachedPayload = this.redisTemplate.opsForValue().get(CACHE_KEY);
                redisAvailable = true;
            } catch (Exception ex) {
                redisError = ex.getMessage();
            }

            String message;
            String source;
            if (StringUtils.hasText(cachedPayload)) {
                message = cachedPayload;
                source = "redis";
            } else {
                message = readFromDisk();
                source = "file";

                if (redisAvailable) {
                    try {
                        this.redisTemplate.opsForValue().set(CACHE_KEY, message);
                        source = "file+redis";
                    } catch (Exception ex) {
                        redisError = ex.getMessage();
                    }
                }
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "storage");
            response.put("status", "OK");
            response.put("timestamp", Instant.now().toString());
            response.put("message", message);
            response.put("source", source);
            response.put("storagePath", this.storagePath.toString());
            response.put("redisAvailable", redisAvailable);
            if (redisError != null) {
                response.put("redisError", redisError);
            }
            return response;
        }

        Map<String, Object> health() {
            boolean fileExists = Files.exists(this.storagePath);
            boolean redisHealthy;
            String redisPing = null;
            String redisError = null;

            try {
                redisPing = this.redisTemplate.execute((RedisCallback<String>) connection -> connection.ping());
                redisHealthy = "PONG".equalsIgnoreCase(redisPing);
            } catch (Exception ex) {
                redisHealthy = false;
                redisError = ex.getMessage();
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "storage");
            response.put("status", fileExists && redisHealthy ? "UP" : "DOWN");
            response.put("timestamp", Instant.now().toString());
            response.put("storagePath", this.storagePath.toString());
            response.put("fileExists", fileExists);
            response.put("redisPing", redisPing);
            if (redisError != null) {
                response.put("redisError", redisError);
            }
            return response;
        }

        private String readFromDisk() {
            try {
                String value = Files.readString(this.storagePath).trim();
                if (StringUtils.hasText(value)) {
                    return value;
                }

                Files.writeString(this.storagePath, DEFAULT_PAYLOAD, StandardOpenOption.TRUNCATE_EXISTING);
                return DEFAULT_PAYLOAD;
            } catch (IOException ex) {
                throw new IllegalStateException("Cannot read file " + this.storagePath, ex);
            }
        }
    }
}
