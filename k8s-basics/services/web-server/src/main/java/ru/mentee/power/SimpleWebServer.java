package ru.mentee.power;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class SimpleWebServer {
    private final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException {
        new SimpleWebServer().start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            // TODO: Главная страница с информацией о сервисе
            String response = "Web-server is working!\nAvailable endpoints: /call-calculator";
            sendResponse(exchange, response);
        });

        server.createContext("/call-calculator", exchange -> {
            // TODO: Вызвать calculator service через Kubernetes DNS
            // Использовать: http://calculator-service/calculate
            // Обработать ответ и вернуть клиенту
            String query = exchange.getRequestURI().getQuery();
            String versionHeader = exchange.getRequestHeaders().getFirst("x-version");
            String calculatorResponse = callCalculatorService(query, versionHeader);
            sendResponse(exchange, calculatorResponse);
        });

        server.start();
        System.out.println("Web Server started on port 8080");
    }

    private String callCalculatorService(String query, String versionHeader) {
        String url = "http://calculator-server-service:8080/calculate";

        if (query != null && !query.isBlank()) {
            url += "?" + query;
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        if (versionHeader != null && !versionHeader.isBlank()) {
            builder.header("x-version", versionHeader);
        }

        HttpRequest request = builder.build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (IOException e) {
            return e.getMessage();
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }
    private void sendResponse(HttpExchange exchange, String response) throws IOException{
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

}