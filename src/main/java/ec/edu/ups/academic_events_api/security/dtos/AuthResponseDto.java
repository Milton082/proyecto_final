package ec.edu.ups.academic_events_api.security.dtos;

import java.util.Set;

import ec.edu.ups.academic_events_api.users.enums.RoleName;

public record AuthResponseDto(
                String accessToken,
                String refreshToken,
                String tokenType,
                long accessExpiresIn,
                long refreshExpiresIn,
                Long userId,
                String email,
                Set<RoleName> roles) {
}
