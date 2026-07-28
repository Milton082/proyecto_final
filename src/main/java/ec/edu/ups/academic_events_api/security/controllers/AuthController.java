package ec.edu.ups.academic_events_api.security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.academic_events_api.core.dtos.MessageResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.AuthResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.CurrentUserResponseDto;
import ec.edu.ups.academic_events_api.security.dtos.LoginRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.LogoutRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterRequestDto;
import ec.edu.ups.academic_events_api.security.dtos.RegisterResponseDto;
import ec.edu.ups.academic_events_api.security.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                authService.login(
                        dto,
                        extractClientIp(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDto> currentUser() {
        return ResponseEntity.ok(
                authService.currentUser());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                authService.refresh(
                        dto,
                        extractClientIp(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(
            @Valid @RequestBody LogoutRequestDto dto) {
        return ResponseEntity.ok(
                authService.logout(dto));
    }

    private String extractClientIp(
            HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}