package ru.mentee.power.processing;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@SpringBootApplication
@RestController
public class ProcessingService {
    private final RestClient storageClient;
    private final String storageUrl;

    public ProcessingService(@Value("${services.storage-url:http://storage:8080}") String storageUrl) {
        this.storageClient = RestClient.builder().baseUrl(storageUrl).build();
        this.storageUrl = storageUrl;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcessingService.class, args);
    }

    @GetMapping(path = "/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> process() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> storageResponse = this.storageClient.get()
                    .uri("/storage/data")
                    .retrieve()
                    .body(Map.class);

            Object payload = storageResponse == null ? null : storageResponse.get("message");
            String originalMessage = payload == null ? "storage payload is missing" : payload.toString();
            String processedMessage = "Processed payload: " + originalMessage.toUpperCase(Locale.ROOT);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "processing");
            response.put("status", "OK");
            response.put("timestamp", Instant.now().toString());
            response.put("storageEndpoint", this.storageUrl + "/storage/data");
            response.put("originalMessage", originalMessage);
            response.put("processedMessage", processedMessage);
            response.put("storage", storageResponse);
            return ResponseEntity.ok(response);
        } catch (RestClientException ex) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "processing");
            response.put("status", "ERROR");
            response.put("timestamp", Instant.now().toString());
            response.put("message", "Cannot reach storage service");
            response.put("details", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
        }
    }

    @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "processing");
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("storageEndpoint", this.storageUrl);
        return response;
    }
}
