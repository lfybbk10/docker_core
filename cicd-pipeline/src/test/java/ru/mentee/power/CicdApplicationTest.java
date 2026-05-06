package ru.mentee.power;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тестирование CI/CD приложения")
class CicdApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Должен вернуть информацию о приложении")
    void shouldReturnAppInfo() {
        var response = restTemplate.getForObject("/", CicdApplication.AppInfo.class);

        assertThat(response).isNotNull();
        assertThat(response.version()).isNotBlank();
        assertThat(response.deployTime()).isNotBlank();
        assertThat(response.hostname()).isNotBlank();
    }

    @Test
    @DisplayName("Должен вернуть статус здоровья")
    void shouldReturnHealthStatus() {
        var response = restTemplate.getForObject("/health", CicdApplication.HealthStatus.class);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("UP");
        assertThat(response.uptime()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("Pipeline должен запускаться при push в main")
    void shouldTriggerPipeline_onMainPush() throws IOException {
        var workflow = Files.readString(Path.of(".github/workflows/deploy.yml"));

        assertThat(workflow).contains("push:");
        assertThat(workflow).contains("branches: [main, develop]");
        assertThat(workflow).contains("if: github.ref == 'refs/heads/main'");
    }
}

