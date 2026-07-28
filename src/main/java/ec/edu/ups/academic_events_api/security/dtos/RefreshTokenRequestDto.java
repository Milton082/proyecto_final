package ec.edu.ups.academic_events_api.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(

        @NotBlank(message = "El refresh token es obligatorio") String refreshToken

) {
}