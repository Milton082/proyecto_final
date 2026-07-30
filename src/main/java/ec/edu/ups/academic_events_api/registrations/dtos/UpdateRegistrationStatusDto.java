package ec.edu.ups.academic_events_api.registrations.dtos;

import jakarta.validation.constraints.NotBlank;

public class UpdateRegistrationStatusDto {

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    public UpdateRegistrationStatusDto() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}