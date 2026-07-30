package ec.edu.ups.academic_events_api.registrations.entities;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_registrations_event_participant",
                        columnNames = {"event_id", "participant_id"}
                )
        }
)
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "registration_code",
            nullable = false,
            unique = true
    )
    private UUID registrationCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participant_id",
            nullable = false
    )
    private UserEntity participant;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(
            name = "registered_at",
            nullable = false
    )
    private OffsetDateTime registeredAt;

    @Column(
            name = "status_updated_at",
            nullable = false
    )
    private OffsetDateTime statusUpdatedAt;

    @Column(
            name = "confirmed_at"
    )
    private OffsetDateTime confirmedAt;

    @Column(
            name = "cancelled_at"
    )
    private OffsetDateTime cancelledAt;

    @Version
    private Long version;

    public RegistrationEntity() {
    }

    @PrePersist
    protected void onCreate(){
        OffsetDateTime now = OffsetDateTime.now();

        if(registrationCode == null){
            registrationCode = UUID.randomUUID();
        }

        if(registeredAt == null){
            registeredAt = now;
        }

        if(statusUpdatedAt == null){
            statusUpdatedAt = now;
        }

        if(status == null){
            status = "PENDING";
        }
    }

    public Long getId() {
        return id;
    }

    public UUID getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(UUID registrationCode) {
        this.registrationCode = registrationCode;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public UserEntity getParticipant() {
        return participant;
    }

    public void setParticipant(UserEntity participant) {
        this.participant = participant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public OffsetDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(OffsetDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(OffsetDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Long getVersion() {
        return version;
    }
}