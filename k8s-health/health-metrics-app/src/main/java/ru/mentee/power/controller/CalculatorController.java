package ru.mentee.power.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.domain.Result;
import ru.mentee.power.service.AvailabilityService;
import ru.mentee.power.service.CalculationService;

@RestController
public class CalculatorController {
    private final CalculationService calculationService;
    private final AvailabilityService availabilityService;

    public CalculatorController(CalculationService calculationService, AvailabilityService availabilityService) {
        this.calculationService = calculationService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/calc")
    public Result calc(@RequestParam int a, @RequestParam int b, @RequestParam String op) {
        return calculationService.calculate(a, b, op);
    }

    @GetMapping("/set_liveness")
    public ResponseEntity<String> setLiveness(@RequestParam boolean state) {
        availabilityService.changeLivenessState(state);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/set_readiness")
    public ResponseEntity<String> setReadiness(@RequestParam boolean state) {
        availabilityService.changeReadinessState(state);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
