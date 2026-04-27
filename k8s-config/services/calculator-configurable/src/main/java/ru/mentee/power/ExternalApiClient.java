package ru.mentee.power;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExternalApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private String apiKey;
    private String apiUrl;

    public ExternalApiClient(String apiKey, String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String callExternalApi(String endpoint) {
        String url = this.apiUrl + endpoint;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer " + apiKey).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        }
    }

    public boolean validateApiKey() {
        try{
            callExternalApi("/validate");
            return true;
        } catch (Exception e){
            return false;
        }
    }
}