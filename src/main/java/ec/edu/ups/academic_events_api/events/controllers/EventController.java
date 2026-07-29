package ec.edu.ups.academic_events_api.events.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.services.EventService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> findOne(
            @PathVariable Long id) {
        return ResponseEntity.ok(eventService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> create(
            @Valid @RequestBody CreateEventDto dto) {
        EventResponseDto createdEvent = eventService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventDto dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventResponseDto>> findByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                eventService.findByCategory(categoryId));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponseDto>> findByOrganizer(
            @PathVariable Long organizerId) {
        return ResponseEntity.ok(
                eventService.findByOrganizer(organizerId));
    }
}