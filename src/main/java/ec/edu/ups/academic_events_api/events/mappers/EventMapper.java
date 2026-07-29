package ec.edu.ups.academic_events_api.events.mappers;

import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventEntity toEntity(CreateEventDto dto) {
        EventEntity entity = new EventEntity();

        entity.setTitle(dto.getTitle().trim());
        entity.setDescription(normalizeText(dto.getDescription()));
        entity.setLocation(dto.getLocation().trim());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCapacity(dto.getCapacity());
        entity.setAvailableCapacity(dto.getCapacity());
        entity.setStatus(dto.getStatus().trim().toUpperCase());

        return entity;
    }

    public static void updateEntity(
            EventEntity entity,
            UpdateEventDto dto
    ) {
        entity.setTitle(dto.getTitle().trim());
        entity.setDescription(normalizeText(dto.getDescription()));
        entity.setLocation(dto.getLocation().trim());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus().trim().toUpperCase());
    }

    public static EventResponseDto toResponseDto(
            EventEntity entity
    ) {
        EventResponseDto dto = new EventResponseDto();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setLocation(entity.getLocation());
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

    private static String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}