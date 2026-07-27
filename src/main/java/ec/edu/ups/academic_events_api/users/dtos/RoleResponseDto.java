package ec.edu.ups.academic_events_api.users.dtos;

import java.time.OffsetDateTime;

import ec.edu.ups.academic_events_api.users.enums.RoleName;

public record RoleResponseDto(
                Long id,
                RoleName name,
                String description,
                OffsetDateTime createdAt) {
}
