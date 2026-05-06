package ru.mentee.power;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootApplication
public class HealthMetricsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthMetricsAppApplication.class, args);
	}
}