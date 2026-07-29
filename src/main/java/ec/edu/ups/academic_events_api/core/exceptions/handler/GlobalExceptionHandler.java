package ec.edu.ups.academic_events_api.core.exceptions.handler;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import ec.edu.ups.academic_events_api.core.exceptions.base.ApplicationException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.RateLimitException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.TooManyRequestsException;
import ec.edu.ups.academic_events_api.core.exceptions.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /*
         * Excepciones controladas del proyecto.
         * Ejemplo: NotFoundException, ConflictException y BadRequestException.
         */
        @ExceptionHandler(ApplicationException.class)
        public ResponseEntity<ErrorResponse> handleApplicationException(
                        ApplicationException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                exception.getStatus(),
                                exception.getErrorCode(),
                                exception.getMessage(),
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(exception.getStatus())
                                .body(response);
        }

        /*
         * Rate limiting.
         * Incluye el encabezado Retry-After.
         */
        @ExceptionHandler(RateLimitException.class)
        public ResponseEntity<ErrorResponse> handleRateLimit(
                        RateLimitException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.TOO_MANY_REQUESTS,
                                exception.getErrorCode(),
                                exception.getMessage(),
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.TOO_MANY_REQUESTS)
                                .header(
                                                HttpHeaders.RETRY_AFTER,
                                                String.valueOf(
                                                                exception.getRetryAfterSeconds()))
                                .body(response);
        }

        /*
         * Errores de @Valid.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                Map<String, String> fieldErrors = new LinkedHashMap<>();

                for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {

                        fieldErrors.putIfAbsent(
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage());
                }

                ErrorResponse response = buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "VALIDATION_ERROR",
                                "Existen campos inválidos",
                                request.getRequestURI(),
                                fieldErrors);

                return ResponseEntity
                                .badRequest()
                                .body(response);
        }

        /*
         * JSON mal escrito o enum inválido.
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleUnreadableBody(
                        HttpMessageNotReadableException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "MALFORMED_REQUEST",
                                "El cuerpo de la solicitud es inválido",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .badRequest()
                                .body(response);
        }

        /*
         * Parámetro requerido ausente.
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingParameter(
                        MissingServletRequestParameterException exception,
                        HttpServletRequest request) {
                String message = "El parámetro "
                                + exception.getParameterName()
                                + " es obligatorio";

                ErrorResponse response = buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "MISSING_PARAMETER",
                                message,
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .badRequest()
                                .body(response);
        }

        /*
         * Tipo incorrecto en path variable o query parameter.
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(
                        MethodArgumentTypeMismatchException exception,
                        HttpServletRequest request) {
                String message = "El valor del parámetro "
                                + exception.getName()
                                + " no es válido";

                ErrorResponse response = buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_PARAMETER",
                                message,
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .badRequest()
                                .body(response);
        }

        /*
         * Mensaje genérico para login.
         * No revela si el correo existe.
         */
        @ExceptionHandler({
                        BadCredentialsException.class,
                        AuthenticationCredentialsNotFoundException.class,
                        LockedException.class,
                        DisabledException.class
        })
        public ResponseEntity<ErrorResponse> handleAuthentication(
                        RuntimeException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.UNAUTHORIZED,
                                "INVALID_CREDENTIALS",
                                "Correo o contraseña incorrectos",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(response);
        }

        /*
         * Falta de permisos.
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                        AccessDeniedException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.FORBIDDEN,
                                "FORBIDDEN",
                                "No tiene permisos para realizar esta operación",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(response);
        }

        /*
         * Restricciones UNIQUE, FK, NOT NULL, etc.
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrity(
                        DataIntegrityViolationException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.CONFLICT,
                                "DATA_INTEGRITY_VIOLATION",
                                "La operación no pudo completarse porque entra en conflicto con los datos existentes",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(response);
        }

        /*
         * Método HTTP incorrecto.
         */
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse> handleMethodNotSupported(
                        HttpRequestMethodNotSupportedException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.METHOD_NOT_ALLOWED,
                                "METHOD_NOT_ALLOWED",
                                "El método HTTP utilizado no está permitido para este recurso",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(response);
        }

        /*
         * Endpoint inexistente.
         */
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoHandler(
                        NoHandlerFoundException exception,
                        HttpServletRequest request) {
                ErrorResponse response = buildResponse(
                                HttpStatus.NOT_FOUND,
                                "ENDPOINT_NOT_FOUND",
                                "El endpoint solicitado no existe",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        /*
         * Error inesperado.
         * Registra el detalle completo en el servidor,
         * pero no lo expone al cliente.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(
                        Exception exception,
                        HttpServletRequest request) {
                log.error(
                                "Error inesperado procesando {}",
                                request.getRequestURI(),
                                exception);

                ErrorResponse response = buildResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INTERNAL_SERVER_ERROR",
                                "Ocurrió un error interno en el servidor",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        private ErrorResponse buildResponse(
                        HttpStatus status,
                        String errorCode,
                        String message,
                        String path,
                        Map<String, String> validationErrors) {
                return new ErrorResponse(
                                OffsetDateTime.now(),
                                status.value(),
                                errorCode,
                                message,
                                path,
                                validationErrors);
        }

        @ExceptionHandler(TooManyRequestsException.class)
        public ResponseEntity<ErrorResponse> handleTooManyRequests(
                        TooManyRequestsException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildResponse(
                                HttpStatus.TOO_MANY_REQUESTS,
                                "TOO_MANY_REQUESTS",
                                exception.getMessage(),
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(errorResponse);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoResourceFound(
                        NoResourceFoundException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildResponse(
                                HttpStatus.NOT_FOUND,
                                "ENDPOINT_NOT_FOUND",
                                "El recurso solicitado no existe",
                                request.getRequestURI(),
                                null);

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(errorResponse);
        }
}