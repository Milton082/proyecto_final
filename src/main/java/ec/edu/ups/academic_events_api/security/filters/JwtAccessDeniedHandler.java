package ec.edu.ups.academic_events_api.security.filters;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler
                implements AccessDeniedHandler {

        @Override
        public void handle(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                String json = """
                                {
                                  "timestamp": "%s",
                                  "status": 403,
                                  "errorCode": "FORBIDDEN",
                                  "message": "No tiene permisos para acceder a este recurso",
                                  "path": "%s"
                                }
                                """.formatted(
                                OffsetDateTime.now(),
                                escapeJson(request.getRequestURI()));

                response.getWriter().write(json);
        }

        private String escapeJson(String value) {
                if (value == null) {
                        return "";
                }

                return value
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"");
        }
}