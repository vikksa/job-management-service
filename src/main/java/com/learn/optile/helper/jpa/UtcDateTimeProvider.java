package com.learn.optile.helper.jpa;

import org.springframework.data.auditing.DateTimeProvider;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * Implementation of {@link DateTimeProvider} returning date time's in the UTC time zone.
 */
public class UtcDateTimeProvider implements DateTimeProvider {

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(Instant.now());
    }

}
