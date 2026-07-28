package ec.edu.ups.academic_events_api.registrations.services;


import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.dtos.CreateRegistrationDto;
import ec.edu.ups.academic_events_api.registrations.dtos.RegistrationResponseDto;
import ec.edu.ups.academic_events_api.registrations.dtos.UpdateRegistrationStatusDto;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.registrations.mappers.RegistrationMapper;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.OffsetDateTime;
import java.util.List;


@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationServiceImpl(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponseDto> findAll() {

        return registrationRepository.findAll()
                .stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationResponseDto findOne(Long id) {
        RegistrationEntity registration =
                findRegistrationById(id);

        return RegistrationMapper.toResponseDto(registration);
    }

    @Override
    public RegistrationResponseDto create(
            CreateRegistrationDto dto
    ) {

        if(registrationRepository
                .existsByEventIdAndParticipantId(
                        dto.getEventId(),
                        dto.getParticipantId()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya está registrado en este evento"
            );
        }

        EventEntity event =
                findEventById(dto.getEventId());
        UserEntity participant =
                findUserById(dto.getParticipantId());

        RegistrationEntity registration =
                new RegistrationEntity();
        registration.setEvent(event);
        registration.setParticipant(participant);
        registration.setStatus("PENDING");
        RegistrationEntity saved =
                registrationRepository.save(registration);

        return RegistrationMapper.toResponseDto(saved);
    }

    @Override
    public RegistrationResponseDto updateStatus(
            Long id,
            UpdateRegistrationStatusDto dto
    ) {

        RegistrationEntity registration =
                findRegistrationById(id);

        String status =
                dto.getStatus()
                .trim()
                .toUpperCase();

        OffsetDateTime now =
                OffsetDateTime.now();

        switch(status){
            case "CONFIRMED":
                registration.setConfirmedAt(now);
                registration.setCancelledAt(null);
                break;
            case "CANCELLED":
                registration.setCancelledAt(now);
                registration.setConfirmedAt(null);
                break;
            case "PENDING":
                registration.setConfirmedAt(null);
                registration.setCancelledAt(null);
                break;
            case "REJECTED":
                registration.setConfirmedAt(null);
                registration.setCancelledAt(null);
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estado de inscripción inválido"
                );
        }
        registration.setStatus(status);
        registration.setStatusUpdatedAt(now);
        RegistrationEntity updated =
                registrationRepository.save(registration);

        return RegistrationMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        RegistrationEntity registration =
                findRegistrationById(id);
        registrationRepository.delete(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponseDto> findByEvent(
            Long eventId
    ) {
        findEventById(eventId);

        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponseDto> findByParticipant(
            Long participantId
    ) {
        findUserById(participantId);

        return registrationRepository.findByParticipantId(participantId)
                .stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    private RegistrationEntity findRegistrationById(Long id){

        return registrationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inscripción no encontrada"
                        )
                );
    }

    private EventEntity findEventById(Long id){

        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Evento no encontrado"
                        )
                );
    }

    private UserEntity findUserById(Long id){

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );
    }
}