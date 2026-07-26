package ec.edu.ups.academic_events_api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/actuator/health",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/status/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .build();
        }
}