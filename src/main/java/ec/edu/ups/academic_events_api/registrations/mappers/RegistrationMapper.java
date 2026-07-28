package ec.edu.ups.academic_events_api.registrations.mappers;

import ec.edu.ups.academic_events_api.registrations.dtos.RegistrationResponseDto;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;

public final class RegistrationMapper {

    private RegistrationMapper() {
    }

    public static RegistrationResponseDto toResponseDto(
            RegistrationEntity entity) {

        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setId(entity.getId());
        dto.setRegistrationCode(
                entity.getRegistrationCode());
        dto.setStatus(
                entity.getStatus());
        dto.setRegisteredAt(
                entity.getRegisteredAt());
        dto.setStatusUpdatedAt(
                entity.getStatusUpdatedAt());
        dto.setConfirmedAt(
                entity.getConfirmedAt());
        dto.setCancelledAt(
                entity.getCancelledAt());

        if (entity.getEvent() != null) {
            dto.setEventId(
                    entity.getEvent().getId());
            dto.setEventTitle(
                    entity.getEvent().getTitle());
        }

        if (entity.getParticipant() != null) {
            dto.setParticipantId(
                    entity.getParticipant().getId());
            dto.setParticipantName(
                    entity.getParticipant().getFirstName()
                            + " "
                            + entity.getParticipant().getLastName());
        }
        return dto;
    }
}