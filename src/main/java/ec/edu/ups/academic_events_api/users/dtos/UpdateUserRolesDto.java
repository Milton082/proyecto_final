package ec.edu.ups.academic_events_api.users.dtos;

import java.util.Set;

import ec.edu.ups.academic_events_api.users.enums.RoleName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRolesDto(

        @NotNull(message = "Los roles son obligatorios") @NotEmpty(message = "Debe asignar al menos un rol") Set<RoleName> roles

) {
}