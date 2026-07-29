package ec.edu.ups.academic_events_api.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    private final CorsProperties properties;

    public CorsConfig(CorsProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                safeList(properties.allowedOrigins()));

        configuration.setAllowedMethods(
                safeList(properties.allowedMethods()));

        configuration.setAllowedHeaders(
                safeList(properties.allowedHeaders()));

        configuration.setExposedHeaders(
                safeList(properties.exposedHeaders()));

        configuration.setAllowCredentials(
                properties.allowCredentials());

        configuration.setMaxAge(
                properties.maxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration);

        return source;
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }
}