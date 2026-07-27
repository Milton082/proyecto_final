package ec.edu.ups.academic_events_api.users.dtos;

import ec.edu.ups.academic_events_api.users.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusDto(

                @NotNull(message = "El estado es obligatorio") UserStatus status

) {
}