package ec.edu.ups.academic_events_api.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.swagger")
public record SwaggerSecurityProperties(
        boolean publicEnabled) {
}