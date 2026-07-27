package ec.edu.ups.academic_events_api.core.exceptions.base;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {
    private final HttpStatus status;

    protected ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
