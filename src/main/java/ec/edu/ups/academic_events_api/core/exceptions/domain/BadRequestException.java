package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                message);
    }

    public BadRequestException(
            String errorCode,
            String message) {
        super(
                HttpStatus.BAD_REQUEST,
                errorCode,
                message);
    }
}