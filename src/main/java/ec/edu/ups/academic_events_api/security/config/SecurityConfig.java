package ec.edu.ups.academic_events_api.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
            JwtAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /*
     * Codificador usado tanto por los usuarios reales de la API
     * como por el usuario exclusivo de Swagger.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Proveedor de autenticación para los usuarios almacenados
     * en PostgreSQL.
     */
    @Bean
    public DaoAuthenticationProvider apiAuthenticationProvider(
            @Qualifier("userDetailsServiceImpl")
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    /*
     * AuthenticationManager principal utilizado por /auth/login.
     *
     * Se marca como @Primary porque también existe un manager
     * independiente para Swagger.
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(
            @Qualifier("apiAuthenticationProvider")
            DaoAuthenticationProvider apiAuthenticationProvider
    ) {
        return new ProviderManager(apiAuthenticationProvider);
    }

    /*
     * Usuario exclusivo para acceder a Swagger.
     *
     * Usuario: swagger
     * Contraseña: Swagger123!
     */
    @Bean
    public InMemoryUserDetailsManager swaggerUserDetailsService(
            PasswordEncoder passwordEncoder
    ) {
        UserDetails swaggerUser = User.builder()
                .username("swagger")
                .password(
                        passwordEncoder.encode("Swagger123!")
                )
                .roles("SWAGGER")
                .build();

        return new InMemoryUserDetailsManager(swaggerUser);
    }

    /*
     * Proveedor exclusivo para Swagger.
     *
     * Este proveedor no se usa en /auth/login.
     */
    @Bean
    public DaoAuthenticationProvider swaggerAuthenticationProvider(
            @Qualifier("swaggerUserDetailsService")
            InMemoryUserDetailsManager swaggerUserDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        swaggerUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    /*
     * AuthenticationManager exclusivo para Swagger.
     */
    @Bean
    public AuthenticationManager swaggerAuthenticationManager(
            @Qualifier("swaggerAuthenticationProvider")
            DaoAuthenticationProvider swaggerAuthenticationProvider
    ) {
        return new ProviderManager(
                swaggerAuthenticationProvider
        );
    }

    /*
     * Primera cadena de seguridad.
     *
     * Protege Swagger mediante HTTP Basic.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("swaggerAuthenticationManager")
            AuthenticationManager swaggerAuthenticationManager
    ) throws Exception {

        return http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                )

                .authenticationManager(
                        swaggerAuthenticationManager
                )

                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .hasRole("SWAGGER")
                )

                .httpBasic(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .build();
    }

    /*
     * Segunda cadena de seguridad.
     *
     * Protege el resto de la API mediante JWT.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("authenticationManager")
            AuthenticationManager authenticationManager
    ) throws Exception {

        return http
                .authenticationManager(authenticationManager)

                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .headers(headers -> headers
                        .contentTypeOptions(
                                Customizer.withDefaults()
                        )

                        .frameOptions(frame -> frame.deny())

                        .referrerPolicy(referrer -> referrer
                                .policy(
                                        ReferrerPolicyHeaderWriter
                                                .ReferrerPolicy
                                                .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                )
                        )

                        .permissionsPolicyHeader(permissions -> permissions
                                .policy(
                                        "camera=(), microphone=(), "
                                                + "geolocation=(), payment=()"
                                )
                        )
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                accessDeniedHandler
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/error",
                                "/auth/register",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/logout",
                                "/actuator/health",
                                "/status"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/users/**",
                                "/roles/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/events/**",
                                "/categories/**",
                                "/sessions/**",
                                "/registrations/**",
                                "/reports/**"
                        )
                        .authenticated()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}