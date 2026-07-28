package ec.edu.ups.academic_events_api.registrations.dtos;

import jakarta.validation.constraints.NotNull;

public class CreateRegistrationDto {

    @NotNull(message = "El evento es obligatorio")
    private Long eventId;

    @NotNull(message = "El participante es obligatorio")
    private Long participantId;

    public CreateRegistrationDto() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}