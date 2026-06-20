package ru.mentee.power;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        // TODO: Реализовать получение списка пользователей
        throw new UnsupportedOperationException("Метод getAllUsers не реализован");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        // TODO: Реализовать получение пользователя по ID
        throw new UnsupportedOperationException("Метод getUserById не реализован");
    }

    @PostMapping
    public Map<String, Object> createUser(@RequestBody Map<String, Object> user) {
        // TODO: Реализовать создание пользователя
        throw new UnsupportedOperationException("Метод createUser не реализован");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        // TODO: Реализовать health check endpoint
        throw new UnsupportedOperationException("Метод health не реализован");
    }

    public static void main(String[] args) {
        SpringApplication.run(UserController.class, args);
    }
}