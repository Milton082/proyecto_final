package ec.edu.ups.academic_events_api.core.exceptions.response;

import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(

        OffsetDateTime timestamp,

        int status,

        String errorCode,

        String message,

        String path,

        Map<String, String> validationErrors

) {
}