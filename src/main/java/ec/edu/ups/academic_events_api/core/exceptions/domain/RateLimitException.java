package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class RateLimitException extends ApplicationException {

    private final long retryAfterSeconds;

    public RateLimitException(
            String message,
            long retryAfterSeconds) {
        super(
                HttpStatus.TOO_MANY_REQUESTS,
                "Limite de solicitudes excedido",
                message);

        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}