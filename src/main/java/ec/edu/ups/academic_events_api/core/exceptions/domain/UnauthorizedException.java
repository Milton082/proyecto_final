package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                message);
    }

    public UnauthorizedException(
            String errorCode,
            String message) {
        super(
                HttpStatus.UNAUTHORIZED,
                errorCode,
                message);
    }
}