package ru.mentee.power;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import ru.mentee.power.health.DatabaseHealthIndicator;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование health indicators")
class HealthIndicatorTest {

    @Test
    @DisplayName("Должен вернуть UP когда БД доступна")
    void shouldReturnUp_whenDatabaseIsAvailable() {
        // given
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator();

        // when
        Health health = indicator.health();

        // then
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("database_health");
    }

    @Test
    @DisplayName("Должен вернуть DOWN когда БД недоступна")
    void shouldReturnDown_whenDatabaseIsUnavailable() {
        // given
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator();
        indicator.crashDatabase();

        // when
        Health health = indicator.health();

        // then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("database_health");
    }

    @Test
    @DisplayName("Должен включать время отклика в деталях")
    void shouldIncludeResponseTime_inHealthDetails() {
        // given
        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator();

        // when
        Health health = indicator.health();

        // then
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("response_time");
    }
}