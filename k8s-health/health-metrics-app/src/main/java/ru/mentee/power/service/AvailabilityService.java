package ru.mentee.power.service;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {
    private final ApplicationEventPublisher eventPublisher;

    public AvailabilityService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void changeReadinessState(boolean ready) {
        AvailabilityChangeEvent.publish(
                eventPublisher,
                this,
                ready
                        ? ReadinessState.ACCEPTING_TRAFFIC
                        : ReadinessState.REFUSING_TRAFFIC
        );
    }

    public void changeLivenessState(boolean alive) {
        AvailabilityChangeEvent.publish(
                eventPublisher,
                this,
                alive
                        ? LivenessState.CORRECT
                        : LivenessState.BROKEN
        );
    }
}
