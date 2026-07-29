package ec.edu.ups.academic_events_api.events.services;

import java.util.List;
import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;

public interface EventService {

    List<EventResponseDto> findAll();
    EventResponseDto findOne(Long id);
    EventResponseDto create(CreateEventDto dto);
    EventResponseDto update(
            Long id,
            UpdateEventDto dto,
            Long authenticatedUserId,
            boolean admin
    );

    void delete(
            Long id,
            Long authenticatedUserId,
            boolean admin
    );

    List<EventResponseDto> findByCategory(Long categoryId);
    List<EventResponseDto> findByOrganizer(Long organizerId);
}