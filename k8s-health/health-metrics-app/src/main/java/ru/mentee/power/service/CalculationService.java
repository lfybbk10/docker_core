package ru.mentee.power.service;

import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import ru.mentee.power.domain.Result;
import ru.mentee.power.metrics.BusinessMetrics;

@Service
public class CalculationService {
    private final BusinessMetrics businessMetrics;

    public CalculationService(BusinessMetrics businessMetrics) {
        this.businessMetrics = businessMetrics;
    }

    public Result calculate(int a, int b, String op) {
        Timer.Sample sample = businessMetrics.startTimer();
        businessMetrics.incrementActiveRequests();

        try {
            int result = switch (op) {
                case "add" -> a + b;
                case "subtract" -> a - b;
                case "multiply" -> a * b;
                case "divide" -> a / b;
                default -> throw new IllegalArgumentException("Unknown operation: " + op);
            };

            businessMetrics.recordSuccess(op);

            return new Result(a, b, op, result);
        } catch (Exception e) {
            businessMetrics.recordError(op, e.getClass().getSimpleName());
            throw e;
        }
        finally {
            businessMetrics.decrementActiveRequests();
            businessMetrics.stopTimer(sample);
        }
    }

}
