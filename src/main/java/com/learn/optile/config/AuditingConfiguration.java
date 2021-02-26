package com.learn.optile.config;

import com.learn.optile.helper.jpa.UtcDateTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configures JPA Auditing.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class AuditingConfiguration {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new UtcDateTimeProvider();
    }

}
