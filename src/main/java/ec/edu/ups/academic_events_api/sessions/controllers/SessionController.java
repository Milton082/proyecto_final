package ec.edu.ups.academic_events_api.sessions.controllers;

import ec.edu.ups.academic_events_api.sessions.dtos.CreateSessionDto;
import ec.edu.ups.academic_events_api.sessions.dtos.SessionResponseDto;
import ec.edu.ups.academic_events_api.sessions.dtos.UpdateSessionDto;
import ec.edu.ups.academic_events_api.sessions.services.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponseDto>> findAll() {
        return ResponseEntity.ok(sessionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDto> findOne(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(sessionService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<SessionResponseDto> create(
            @Valid @RequestBody CreateSessionDto dto
    ) {
        SessionResponseDto createdSession = sessionService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSessionDto dto
    ) {
        return ResponseEntity.ok(
                sessionService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        sessionService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SessionResponseDto>> findByEvent(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                sessionService.findByEvent(eventId)
        );
    }
}