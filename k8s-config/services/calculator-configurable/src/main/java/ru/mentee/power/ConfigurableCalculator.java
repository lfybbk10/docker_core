package ru.mentee.power;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Stream;

public class ConfigurableCalculator {
    private Properties config = new Properties();
    private DatabaseConfig databaseConfig;
    private ExternalApiClient externalApiClient;

    public static void main(String[] args) throws IOException {
        new ConfigurableCalculator().start();
    }

    public void start() throws IOException {
        loadConfiguration();
        initDatabase();
        initExternalApiClient();

        int port = Integer.parseInt(getConfig("SERVER_PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String response = "Web-server is working!\nAvailable endpoints: /calculate\n/config\n/external";
            sendResponse(exchange, response);
        });

        server.createContext("/calculate", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            int a = getIntParam(query, "a", 0);
            int b = getIntParam(query, "b", 0);
            int result = a + b;

            String expression = a+" + "+b;
            databaseConfig.saveCalculation(expression, result);
            String response = "expression: " + expression + "\nresult: " + result;
            sendResponse(exchange, response);
        });

        server.createContext("/config", exchange -> {
            // TODO: Показать текущую конфигурацию
            // БЕЗ sensitive данных
            String response = config.toString();
            sendResponse(exchange, response);
        });

        server.createContext("/external", exchange -> {
            System.out.println("External request started");
            String response = externalApiClient.callExternalApi("/get");
            System.out.println("External response: " + response);
            sendResponse(exchange, response);
        });

        server.start();
        System.out.println("Configurable Calculator запущен на порту " + port);
    }

    private void loadConfiguration() {
        loadFileProperties("/app/config/application.properties");
        loadFileProperties("/app/config/database.properties");
    }

    private void loadClasspathProperties(String resourceName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                config.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFileProperties(String path) {
        File file = new File(path);

        if (!file.exists()) {
            return;
        }

        try (InputStream is = new FileInputStream(file)) {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        databaseConfig = new DatabaseConfig();
        String host = getConfig("DATABASE_HOST");
        String port = getConfig("DATABASE_PORT");
        String databaseName = getConfig("DATABASE_NAME");
        String databaseUser = getConfig("DATABASE_USER");
        String databasePassword = getConfig("DATABASE_PASSWORD");
        try {
            databaseConfig.connect(host, port, databaseName, databaseUser, databasePassword);
            databaseConfig.createTables();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initExternalApiClient() {
        externalApiClient = new ExternalApiClient(getConfig("API_KEY"), "https://httpbin.org");
    }

    private String getConfig(String key) {
        if(System.getenv().containsKey(key)) {
            return System.getenv(key);
        }
        else if(config.containsKey(key)) {
            return config.getProperty(key);
        }

        return "";
    }

    private void sendResponse(HttpExchange exchange, String response)
            throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
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
}