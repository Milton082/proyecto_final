package ec.edu.ups.academic_events_api.security.dtos;

import java.util.Set;

import ec.edu.ups.academic_events_api.users.enums.RoleName;
import ec.edu.ups.academic_events_api.users.enums.UserStatus;

public record RegisterResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        Set<RoleName> roles) {
}