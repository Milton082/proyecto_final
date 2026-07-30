package ec.edu.ups.academic_events_api.users.dtos;

import java.time.OffsetDateTime;
import java.util.Set;

import ec.edu.ups.academic_events_api.users.enums.RoleName;
import ec.edu.ups.academic_events_api.users.enums.UserStatus;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        Set<RoleName> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}