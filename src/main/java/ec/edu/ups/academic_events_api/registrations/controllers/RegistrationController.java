package ec.edu.ups.academic_events_api.registrations.controllers;


import ec.edu.ups.academic_events_api.registrations.dtos.CreateRegistrationDto;
import ec.edu.ups.academic_events_api.registrations.dtos.RegistrationResponseDto;
import ec.edu.ups.academic_events_api.registrations.dtos.UpdateRegistrationStatusDto;
import ec.edu.ups.academic_events_api.registrations.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService
    ){
        this.registrationService = registrationService;

    }

    @GetMapping
    public ResponseEntity<List<RegistrationResponseDto>> findAll(){
        return ResponseEntity.ok(
                registrationService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponseDto> findOne(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                registrationService.findOne(id)
        );
    }

    @PostMapping
    public ResponseEntity<RegistrationResponseDto> create(
            @Valid @RequestBody CreateRegistrationDto dto
    ){
        RegistrationResponseDto created =
                registrationService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RegistrationResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRegistrationStatusDto dto
    ){
        return ResponseEntity.ok(
                registrationService.updateStatus(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ){
        registrationService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RegistrationResponseDto>> findByEvent(
            @PathVariable Long eventId
    ){
        return ResponseEntity.ok(
                registrationService.findByEvent(eventId)
        );
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<RegistrationResponseDto>> findByParticipant(
            @PathVariable Long participantId
    ){
        return ResponseEntity.ok(
                registrationService.findByParticipant(participantId)
        );
    }
}