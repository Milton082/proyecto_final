package ec.edu.ups.academic_events_api.events.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ec.edu.ups.academic_events_api.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(
            EventService eventService
    ) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>>
    findAll() {
        return ResponseEntity.ok(
                eventService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> findOne(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                eventService.findOne(id)
        );
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> create(
            @Valid
            @RequestBody
            CreateEventDto dto
    ) {
        EventResponseDto createdEvent =
                eventService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(
            @PathVariable Long id,
            @Valid
            @RequestBody
            UpdateEventDto dto,
            @AuthenticationPrincipal
            UserDetailsImpl currentUser
    ) {
        return ResponseEntity.ok(
                eventService.update(
                        id,
                        dto,
                        currentUser.getId(),
                        isAdmin(currentUser)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal
            UserDetailsImpl currentUser
    ) {
        eventService.delete(
                id,
                currentUser.getId(),
                isAdmin(currentUser)
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventResponseDto>>
    findByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(
                eventService.findByCategory(categoryId)
        );
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponseDto>>
    findByOrganizer(
            @PathVariable Long organizerId
    ) {
        return ResponseEntity.ok(
                eventService.findByOrganizer(organizerId)
        );
    }

    private boolean isAdmin(
            UserDetailsImpl currentUser
    ) {
        if (currentUser == null) {
            return false;
        }

        return currentUser.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(
                                authority.getAuthority()
                        )
                );
    }
}