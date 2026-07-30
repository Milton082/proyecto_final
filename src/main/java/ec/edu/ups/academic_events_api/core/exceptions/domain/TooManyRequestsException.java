package ec.edu.ups.academic_events_api.core.exceptions.domain;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}