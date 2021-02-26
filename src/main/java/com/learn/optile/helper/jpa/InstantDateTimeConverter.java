package com.learn.optile.helper.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Converts {@link Instant} to {@link Timestamp}s and back. Will always return {@link Instant} at UTC
 * timezone.
 */
@Converter(autoApply = true)
public class InstantDateTimeConverter implements AttributeConverter<Instant, Timestamp>, Serializable {
    @Override
    public Timestamp convertToDatabaseColumn(Instant instantDateTime) {
        return instantDateTime != null ? Timestamp.from(instantDateTime) : null;
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }
}
