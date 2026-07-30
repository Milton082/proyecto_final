package ec.edu.ups.academic_events_api.events.mappers;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;

public final class EventMapper {

    private static final String MODALITY_PRESENTIAL = "PRESENTIAL";
    private static final String MODALITY_VIRTUAL = "VIRTUAL";
    private static final String MODALITY_HYBRID = "HYBRID";

    private static final Set<String> VALID_MODALITIES = Set.of(
            MODALITY_PRESENTIAL,
            MODALITY_VIRTUAL,
            MODALITY_HYBRID
    );

    private static final Set<String> VALID_STATUSES = Set.of(
            "DRAFT",
            "PUBLISHED",
            "FINISHED",
            "CANCELLED"
    );

    private EventMapper() {
    }

    public static EventEntity toEntity(CreateEventDto dto) {
        String modality = normalizeUppercase(dto.getModality());
        String status = normalizeUppercase(dto.getStatus());
        String location = normalizeText(dto.getLocation());
        String virtualUrl = normalizeText(dto.getVirtualUrl());

        validateEventData(
                modality,
                location,
                virtualUrl,
                dto.getRegistrationStartDate(),
                dto.getRegistrationEndDate(),
                dto.getStartDate(),
                dto.getEndDate(),
                status
        );

        EventEntity entity = new EventEntity();

        entity.setTitle(dto.getTitle().trim());
        entity.setDescription(dto.getDescription().trim());
        entity.setModality(modality);
        entity.setLocation(location);
        entity.setVirtualUrl(virtualUrl);

        entity.setRegistrationStartDate(
                dto.getRegistrationStartDate()
        );

        entity.setRegistrationEndDate(
                dto.getRegistrationEndDate()
        );

        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        entity.setCapacity(dto.getCapacity());
        entity.setAvailableCapacity(dto.getCapacity());

        entity.setStatus(status);

        return entity;
    }

    public static void updateEntity(
            EventEntity entity,
            UpdateEventDto dto
    ) {
        String modality = normalizeUppercase(dto.getModality());
        String status = normalizeUppercase(dto.getStatus());
        String location = normalizeText(dto.getLocation());
        String virtualUrl = normalizeText(dto.getVirtualUrl());

        validateEventData(
                modality,
                location,
                virtualUrl,
                dto.getRegistrationStartDate(),
                dto.getRegistrationEndDate(),
                dto.getStartDate(),
                dto.getEndDate(),
                status
        );

        updateCapacity(entity, dto.getCapacity());

        entity.setTitle(dto.getTitle().trim());
        entity.setDescription(dto.getDescription().trim());
        entity.setModality(modality);
        entity.setLocation(location);
        entity.setVirtualUrl(virtualUrl);

        entity.setRegistrationStartDate(
                dto.getRegistrationStartDate()
        );

        entity.setRegistrationEndDate(
                dto.getRegistrationEndDate()
        );

        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(status);
    }

    public static EventResponseDto toResponseDto(
            EventEntity entity
    ) {
        EventResponseDto dto = new EventResponseDto();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setModality(entity.getModality());
        dto.setLocation(entity.getLocation());
        dto.setVirtualUrl(entity.getVirtualUrl());

        dto.setRegistrationStartDate(
                entity.getRegistrationStartDate()
        );

        dto.setRegistrationEndDate(
                entity.getRegistrationEndDate()
        );

        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCapacity(entity.getCapacity());
        dto.setAvailableCapacity(entity.getAvailableCapacity());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }

        if (entity.getOrganizer() != null) {
            dto.setOrganizerId(entity.getOrganizer().getId());
        }

        return dto;
    }

    private static void validateEventData(
            String modality,
            String location,
            String virtualUrl,
            LocalDateTime registrationStartDate,
            LocalDateTime registrationEndDate,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String status
    ) {
        validateModality(modality);
        validateStatus(status);

        validateModalityData(
                modality,
                location,
                virtualUrl
        );

        validateDates(
                registrationStartDate,
                registrationEndDate,
                startDate,
                endDate
        );
    }

    private static void validateModality(String modality) {
        if (!VALID_MODALITIES.contains(modality)) {
            throw badRequest(
                    "La modalidad debe ser PRESENTIAL, VIRTUAL o HYBRID"
            );
        }
    }

    private static void validateStatus(String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw badRequest(
                    "El estado debe ser DRAFT, PUBLISHED, "
                            + "FINISHED o CANCELLED"
            );
        }
    }

    private static void validateModalityData(
            String modality,
            String location,
            String virtualUrl
    ) {
        switch (modality) {
            case MODALITY_PRESENTIAL -> {
                if (location == null) {
                    throw badRequest(
                            "La ubicación es obligatoria para "
                                    + "eventos presenciales"
                    );
                }

                if (virtualUrl != null) {
                    throw badRequest(
                            "Un evento presencial no debe tener URL virtual"
                    );
                }
            }

            case MODALITY_VIRTUAL -> {
                if (virtualUrl == null) {
                    throw badRequest(
                            "La URL virtual es obligatoria para "
                                    + "eventos virtuales"
                    );
                }

                if (location != null) {
                    throw badRequest(
                            "Un evento virtual no debe tener ubicación física"
                    );
                }
            }

            case MODALITY_HYBRID -> {
                if (location == null || virtualUrl == null) {
                    throw badRequest(
                            "Un evento híbrido requiere ubicación "
                                    + "y URL virtual"
                    );
                }
            }

            default -> throw badRequest("Modalidad no válida");
        }
    }

    private static void validateDates(
            LocalDateTime registrationStartDate,
            LocalDateTime registrationEndDate,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        if (!registrationStartDate.isBefore(registrationEndDate)) {
            throw badRequest(
                    "La fecha de inicio de inscripciones debe ser "
                            + "anterior a la fecha de cierre"
            );
        }

        boolean registrationEndsBeforeEvent =
                registrationEndDate.isBefore(startDate);

        boolean registrationEndsWhenEventStarts =
                registrationEndDate.isEqual(startDate);

        if (!registrationEndsBeforeEvent
                && !registrationEndsWhenEventStarts) {
            throw badRequest(
                    "Las inscripciones deben cerrar antes o exactamente "
                            + "cuando inicia el evento"
            );
        }

        if (!startDate.isBefore(endDate)) {
            throw badRequest(
                    "La fecha de inicio del evento debe ser "
                            + "anterior a la fecha de finalización"
            );
        }
    }

    private static void updateCapacity(
            EventEntity entity,
            Integer newCapacity
    ) {
        Integer currentCapacity = entity.getCapacity();
        Integer currentAvailable = entity.getAvailableCapacity();

        if (currentCapacity == null || currentAvailable == null) {
            entity.setCapacity(newCapacity);
            entity.setAvailableCapacity(newCapacity);
            return;
        }

        int occupiedPlaces = currentCapacity - currentAvailable;

        if (newCapacity < occupiedPlaces) {
            throw badRequest(
                    "La nueva capacidad no puede ser menor que la "
                            + "cantidad de cupos ocupados: "
                            + occupiedPlaces
            );
        }

        entity.setCapacity(newCapacity);
        entity.setAvailableCapacity(
                newCapacity - occupiedPlaces
        );
    }

    private static String normalizeUppercase(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toUpperCase();
    }

    private static String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private static ResponseStatusException badRequest(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}