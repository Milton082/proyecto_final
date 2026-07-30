package ec.edu.ups.academic_events_api.events.dtos;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateEventDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(
            max = 150,
            message = "El título no puede superar los 150 caracteres"
    )
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(
            max = 2000,
            message = "La descripción no puede superar los 2000 caracteres"
    )
    private String description;

    @NotBlank(message = "La modalidad es obligatoria")
    @Size(
            max = 20,
            message = "La modalidad no puede superar los 20 caracteres"
    )
    private String modality;

    @Size(
            max = 200,
            message = "La ubicación no puede superar los 200 caracteres"
    )
    private String location;

    @Size(
            max = 500,
            message = "La URL virtual no puede superar los 500 caracteres"
    )
    private String virtualUrl;

    @NotNull(message = "La fecha de inicio de inscripciones es obligatoria")
    private LocalDateTime registrationStartDate;

    @NotNull(message = "La fecha de cierre de inscripciones es obligatoria")
    private LocalDateTime registrationEndDate;

    @NotNull(message = "La fecha de inicio del evento es obligatoria")
    @Future(message = "La fecha de inicio del evento debe ser futura")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de finalización del evento es obligatoria")
    @Future(message = "La fecha de finalización del evento debe ser futura")
    private LocalDateTime endDate;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(
            value = 1,
            message = "La capacidad debe ser mayor que cero"
    )
    private Integer capacity;

    @NotBlank(message = "El estado es obligatorio")
    @Size(
            max = 30,
            message = "El estado no puede superar los 30 caracteres"
    )
    private String status;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El organizador es obligatorio")
    private Long organizerId;

    public CreateEventDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVirtualUrl() {
        return virtualUrl;
    }

    public void setVirtualUrl(String virtualUrl) {
        this.virtualUrl = virtualUrl;
    }

    public LocalDateTime getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(
            LocalDateTime registrationStartDate
    ) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDateTime getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(
            LocalDateTime registrationEndDate
    ) {
        this.registrationEndDate = registrationEndDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}