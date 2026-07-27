package ec.edu.ups.academic_events_api.core.exceptions.domain;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}