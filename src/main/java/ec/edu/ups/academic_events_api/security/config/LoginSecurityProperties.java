package ec.edu.ups.academic_events_api.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.login")
public record LoginSecurityProperties(
        long rateLimitMaxRequests,
        long rateLimitWindowSeconds,
        long maxFailedAttempts,
        long failedAttemptWindowSeconds,
        long blockDurationSeconds) {
}
