package ec.edu.ups.academic_events_api.core.exceptions.domain;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}