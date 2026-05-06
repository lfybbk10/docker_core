package ru.mentee.power.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BusinessMetrics {
    private static final String METRIC_NAME = "calculator.operations";

    private final Timer operationTimer;
    private final AtomicInteger activeRequests;
    private final MeterRegistry meterRegistry;


    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.activeRequests = new AtomicInteger(0);

        operationTimer = Timer.builder("calculator.operation.duration")
                .register(meterRegistry);

        Gauge.builder("calculator.active.requests", activeRequests, AtomicInteger::get)
                .description("Number of active requests in progress")
                .register(meterRegistry);
    }

    public void recordSuccess(String operation) {
        meterRegistry.counter(
                METRIC_NAME,
                "operation", operation,
                "status", "success"
        ).increment();

    }

    public void recordError(String operation, String errorType) {
        System.out.println("ERROR METRIC: " + operation + " / " + errorType);

        meterRegistry.counter(
                METRIC_NAME,
                "operation", operation,
                "status", "error",
                "error_type", errorType
        ).increment();
    }

    public void incrementActiveRequests() {
        activeRequests.incrementAndGet();
    }

    public void decrementActiveRequests() {
        activeRequests.decrementAndGet();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(operationTimer);
    }
}
