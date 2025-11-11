import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        // Создаем HTTP сервер на порту 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Обработчик главной страницы
        server.createContext("/", exchange -> {
            String response = "Hello from Docker! Текущее время: " + LocalDateTime.now();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // Обработчик health check
        server.createContext("/health", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.start();
        System.out.println("Сервер запущен на порту 8080");
        System.out.println("Откройте http://localhost:8080 в браузере");
    }
}