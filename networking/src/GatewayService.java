package ru.mentee.power.gateway;

import java.time.Instant;
import java.util.LinkedHashMap;
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
public class GatewayService {
    private final RestClient processingClient;
    private final String processingUrl;

    public GatewayService(@Value("${services.processing-url:http://processing:8080}") String processingUrl) {
        this.processingClient = RestClient.builder().baseUrl(processingUrl).build();
        this.processingUrl = processingUrl;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayService.class, args);
    }

    @GetMapping(path = "/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> process() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> processingResponse = this.processingClient.get()
                    .uri("/process")
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "gateway");
            response.put("status", "OK");
            response.put("timestamp", Instant.now().toString());
            response.put("processingEndpoint", this.processingUrl + "/process");
            response.put("processing", processingResponse);
            return ResponseEntity.ok(response);
        } catch (RestClientException ex) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("service", "gateway");
            response.put("status", "ERROR");
            response.put("timestamp", Instant.now().toString());
            response.put("message", "Cannot reach processing service");
            response.put("details", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
        }
    }

    @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "gateway");
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("processingEndpoint", this.processingUrl);
        return response;
    }
}
