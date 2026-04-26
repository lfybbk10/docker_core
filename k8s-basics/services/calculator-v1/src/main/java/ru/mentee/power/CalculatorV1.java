package ru.mentee.power;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CalculatorV1 {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/calculate", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                send(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            int a = getIntParam(query, "a", 0);
            int b = getIntParam(query, "b", 0);
            int result = a + b;

            String hostname = InetAddress.getLocalHost().getHostName();

            String json = String.format(
                    "{\"result\":%d,\"version\":\"v1\",\"hostname\":\"%s\"}",
                    result,
                    hostname
            );

            send(exchange, 200, json);
        });

        server.createContext("/health", exchange -> {
            send(exchange, 200, "{\"status\":\"OK\",\"version\":\"v1\"}");
        });

        server.start();
        System.out.println("Calculator V1 запущен на порту 8080");
    }

    private static int getIntParam(String query, String name, int defaultValue) {
        if (query == null || query.isBlank()) {
            return defaultValue;
        }

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);

            if (pair.length == 2 && pair[0].equals(name)) {
                try {
                    String decoded = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    return Integer.parseInt(decoded);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }

        return defaultValue;
    }

    private static void send(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String response)
            throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}