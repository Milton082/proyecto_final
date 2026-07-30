package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(
                HttpStatus.CONFLICT,
                "RESOURCE_CONFLICT",
                message);
    }

    public ConflictException(
            String errorCode,
            String message) {
        super(
                HttpStatus.CONFLICT,
                errorCode,
                message);
    }
}