package ec.edu.ups.academic_events_api.core.exceptions.domain;

import org.springframework.http.HttpStatus;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                message);
    }

    public NotFoundException(
            String errorCode,
            String message) {
        super(
                HttpStatus.NOT_FOUND,
                errorCode,
                message);
    }
}