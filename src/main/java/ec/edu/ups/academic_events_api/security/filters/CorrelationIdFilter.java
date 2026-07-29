package ec.edu.ups.academic_events_api.security.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Correlation-ID";

    private static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = resolveCorrelationId(request);

        // Guarda el identificador en la solicitud para que
        // AuditServiceImpl pueda recuperarlo.
        request.setAttribute(
                HEADER_NAME,
                correlationId);

        // Devuelve el mismo identificador en la respuesta.
        response.setHeader(
                HEADER_NAME,
                correlationId);

        // Permite incluir el identificador en los logs técnicos.
        MDC.put(
                MDC_KEY,
                correlationId);

        try {
            filterChain.doFilter(
                    request,
                    response);
        } finally {
            // Evita que el identificador se reutilice
            // en otra solicitud del mismo hilo.
            MDC.remove(MDC_KEY);
        }
    }

    private String resolveCorrelationId(
            HttpServletRequest request) {
        String receivedCorrelationId = request.getHeader(HEADER_NAME);

        if (isValidCorrelationId(receivedCorrelationId)) {
            return UUID.fromString(
                    receivedCorrelationId.trim()).toString();
        }

        return UUID.randomUUID().toString();
    }

    private boolean isValidCorrelationId(
            String correlationId) {
        if (correlationId == null
                || correlationId.isBlank()) {
            return false;
        }

        try {
            UUID.fromString(correlationId.trim());
            return true;

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}