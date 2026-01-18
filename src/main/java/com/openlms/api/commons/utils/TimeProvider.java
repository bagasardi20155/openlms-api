package com.openlms.api.commons.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

@Component
public class TimeProvider {
    private final Clock clock;
    
    public TimeProvider() {
        this.clock = Clock.systemUTC();
    }

    // for tests
    public TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public Instant nowInstant() {
        return Instant.now(clock);
    }

    public ZoneId zone() {
        return clock.getZone();
    }
}
