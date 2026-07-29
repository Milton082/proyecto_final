package ec.edu.ups.academic_events_api.security.filters;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint
                implements AuthenticationEntryPoint {

        @Override
        public void commence(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                String json = """
                                {
                                  "timestamp": "%s",
                                  "status": 401,
                                  "errorCode": "UNAUTHORIZED",
                                  "message": "Debe autenticarse para acceder a este recurso",
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