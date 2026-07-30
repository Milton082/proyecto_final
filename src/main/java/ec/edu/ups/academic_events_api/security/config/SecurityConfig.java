package ec.edu.ups.academic_events_api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

        /*
         * Codificador utilizado tanto por los usuarios de la API
         * como por el usuario de Swagger.
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /*
         * AuthenticationManager utilizado por /auth/login.
         */
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        /*
         * Usuario exclusivo para entrar a Swagger.
         *
         * Usuario: swagger
         * Contraseña: Swagger123!
         */
        @Bean
        public InMemoryUserDetailsManager swaggerUserDetailsService(
                        PasswordEncoder passwordEncoder) {

                UserDetails swaggerUser = User.builder()
                                .username("swagger")
                                .password(passwordEncoder.encode("Swagger123!"))
                                .roles("SWAGGER")
                                .build();

                return new InMemoryUserDetailsManager(swaggerUser);
        }

        /*
         * Proveedor que valida únicamente al usuario de Swagger.
         */
        @Bean
        public DaoAuthenticationProvider swaggerAuthenticationProvider(
                        InMemoryUserDetailsManager swaggerUserDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
                                swaggerUserDetailsService);

                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        /*
         * AuthenticationManager exclusivo para Swagger.
         */
        @Bean
        public AuthenticationManager swaggerAuthenticationManager(
                        DaoAuthenticationProvider swaggerAuthenticationProvider) {

                return new ProviderManager(
                                swaggerAuthenticationProvider);
        }

        /*
         * Primera cadena:
         * protege Swagger mediante HTTP Basic.
         */
        @Bean
        @Order(1)
        public SecurityFilterChain swaggerSecurityFilterChain(
                        HttpSecurity http,
                        AuthenticationManager swaggerAuthenticationManager)
                        throws Exception {

                return http
                                .securityMatcher(
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**")

                                .authenticationManager(
                                                swaggerAuthenticationManager)

                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest()
                                                .hasRole("SWAGGER"))

                                .httpBasic(Customizer.withDefaults())

                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.IF_REQUIRED))

                                .build();
        }

        /*
         * Segunda cadena:
         * protege el resto de la API mediante JWT.
         */
        @Bean
        @Order(2)
        public SecurityFilterChain apiSecurityFilterChain(
                        HttpSecurity http)
                        throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())

                                .cors(Customizer.withDefaults())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                .headers(headers -> headers
                                                .contentTypeOptions(
                                                                Customizer.withDefaults())

                                                .frameOptions(frame -> frame.deny())

                                                .referrerPolicy(referrer -> referrer
                                                                .policy(
                                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                                                .permissionsPolicyHeader(permissions -> permissions
                                                                .policy(
                                                                                "camera=(), microphone=(), "
                                                                                                + "geolocation=(), payment=()")))

                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/error",
                                                                "/auth/register",
                                                                "/auth/login",
                                                                "/auth/refresh",
                                                                "/auth/logout",
                                                                "/actuator/health",
                                                                "/status")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/users/**",
                                                                "/roles/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/events/**",
                                                                "/categories/**",
                                                                "/sessions/**",
                                                                "/registrations/**",
                                                                "/reports/**")
                                                .authenticated()

                                                .anyRequest()
                                                .authenticated())

                                .formLogin(form -> form.disable())

                                .httpBasic(basic -> basic.disable())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .build();
        }
}