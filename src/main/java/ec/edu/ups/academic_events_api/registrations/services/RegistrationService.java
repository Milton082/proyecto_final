package ec.edu.ups.academic_events_api.registrations.services;

import ec.edu.ups.academic_events_api.registrations.dtos.CreateRegistrationDto;
import ec.edu.ups.academic_events_api.registrations.dtos.RegistrationResponseDto;
import ec.edu.ups.academic_events_api.registrations.dtos.UpdateRegistrationStatusDto;
import java.util.List;

public interface RegistrationService {
    
    List<RegistrationResponseDto> findAll();
    RegistrationResponseDto findOne(Long id);
    RegistrationResponseDto create(
            CreateRegistrationDto dto
    );
    RegistrationResponseDto updateStatus(
            Long id,
            UpdateRegistrationStatusDto dto
    );
    void delete(Long id);
    List<RegistrationResponseDto> findByEvent(
            Long eventId
    );
    List<RegistrationResponseDto> findByParticipant(
            Long participantId
    );
}