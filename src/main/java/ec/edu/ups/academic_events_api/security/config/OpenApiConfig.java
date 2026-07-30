package ec.edu.ups.academic_events_api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@Profile("!prod")
public class OpenApiConfig {

        private static final String SECURITY_SCHEME = "bearerAuth";

        @Bean
        public OpenAPI academicEventsOpenApi() {
                SecurityScheme bearerScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT");

                return new OpenAPI()
                                .info(new Info()
                                                .title("Academic Events API")
                                                .description(
                                                                "API para la gestión de eventos académicos")
                                                .version("1.0.0"))
                                .components(new Components()
                                                .addSecuritySchemes(
                                                                SECURITY_SCHEME,
                                                                bearerScheme))
                                .addSecurityItem(
                                                new SecurityRequirement()
                                                                .addList(SECURITY_SCHEME));
        }
}