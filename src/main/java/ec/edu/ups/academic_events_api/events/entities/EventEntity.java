package ec.edu.ups.academic_events_api.events.entities;

import java.time.LocalDateTime;
import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;
import ec.edu.ups.academic_events_api.core.entities.BaseEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class EventEntity extends BaseEntity {

    @Column(
            nullable = false,
            length = 150
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            nullable = false,
            length = 20
    )
    private String modality;

    @Column(length = 200)
    private String location;

    @Column(
            name = "virtual_url",
            length = 500
    )
    private String virtualUrl;

    @Column(
            name = "registration_start_at",
            nullable = false
    )
    private LocalDateTime registrationStartDate;

    @Column(
            name = "registration_end_at",
            nullable = false
    )
    private LocalDateTime registrationEndDate;

    @Column(
            name = "start_at",
            nullable = false
    )
    private LocalDateTime startDate;

    @Column(
            name = "end_at",
            nullable = false
    )
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(
            name = "available_capacity",
            nullable = false
    )
    private Integer availableCapacity;

    @Column(
            nullable = false,
            length = 30
    )
    private String status;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private CategoryEntity category;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "organizer_id",
            nullable = false
    )
    private UserEntity organizer;

    public EventEntity() {
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

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public UserEntity getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserEntity organizer) {
        this.organizer = organizer;
    }
}