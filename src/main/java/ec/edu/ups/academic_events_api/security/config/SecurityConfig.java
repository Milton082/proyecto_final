package ec.edu.ups.academic_events_api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import ec.edu.ups.academic_events_api.security.filters.JwtAccessDeniedHandler;
import ec.edu.ups.academic_events_api.security.filters.JwtAuthenticationEntryPoint;
import ec.edu.ups.academic_events_api.security.filters.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JwtAuthenticationEntryPoint authenticationEntryPoint;
        private final JwtAccessDeniedHandler accessDeniedHandler;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        JwtAuthenticationEntryPoint authenticationEntryPoint,
                        JwtAccessDeniedHandler accessDeniedHandler) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authenticationEntryPoint = authenticationEntryPoint;
                this.accessDeniedHandler = accessDeniedHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                return http
                                // API REST con JWT: no utiliza sesiones ni formularios.
                                .csrf(csrf -> csrf.disable())

                                // Utiliza el CorsConfigurationSource definido en CorsConfig.
                                .cors(Customizer.withDefaults())

                                // La autenticación se maneja mediante tokens JWT.
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // Encabezados de seguridad.
                                .headers(headers -> headers
                                                .contentTypeOptions(
                                                                Customizer.withDefaults())

                                                .frameOptions(frame -> frame.deny())

                                                .referrerPolicy(referrer -> referrer
                                                                .policy(
                                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                                                .permissionsPolicyHeader(permissions -> permissions.policy(
                                                                "camera=(), microphone=(), "
                                                                                + "geolocation=(), payment=()")))

                                // Respuestas uniformes para 401 y 403.
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                // Reglas de acceso por URL.
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/auth/register",
                                                                "/auth/login",
                                                                "/auth/refresh",
                                                                "/actuator/health",
                                                                "/status",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/users/**",
                                                                "/roles/**")
                                                .hasRole("ADMIN")

                                                .anyRequest().authenticated())

                                // No se utilizan formularios ni HTTP Basic.
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())

                                // Ejecuta el filtro JWT antes del filtro estándar.
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .build();
        }
}