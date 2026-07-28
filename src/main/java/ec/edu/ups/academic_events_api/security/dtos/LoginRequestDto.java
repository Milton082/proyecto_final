package ec.edu.ups.academic_events_api.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

        @NotBlank(message = "El correo es obligatorio") @Email(message = "Debe ingresar un correo válido") String email,

        @NotBlank(message = "La contraseña es obligatoria") String password

) {
}