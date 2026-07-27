package ec.edu.ups.academic_events_api.events.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UpdateEventDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String description;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede superar los 200 caracteres")
    private String location;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de finalización es obligatoria")
    @Future(message = "La fecha de finalización debe ser futura")
    private LocalDateTime endDate;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    private Integer capacity;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
    private String status;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El organizador es obligatorio")
    private Long organizerId;

    public UpdateEventDto() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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