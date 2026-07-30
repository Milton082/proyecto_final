package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException(String message) {
        super(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                message);
    }

    public ForbiddenException(
            String errorCode,
            String message) {
        super(
                HttpStatus.FORBIDDEN,
                errorCode,
                message);
    }
}