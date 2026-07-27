package ec.edu.ups.academic_events_api.events.services;

import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import java.util.List;

public interface EventService {

    List<EventResponseDto> findAll();
    EventResponseDto findOne(Long id);
    EventResponseDto create(CreateEventDto dto);
    EventResponseDto update(Long id, UpdateEventDto dto);
    void delete(Long id);
    List<EventResponseDto> findByCategory(Long categoryId);
    List<EventResponseDto> findByOrganizer(Long organizerId);
}