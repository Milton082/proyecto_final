package ec.edu.ups.academic_events_api.registrations.services;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

@Service
@Transactional
public class RegistrationServiceImpl
        implements RegistrationService {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_CONFIRMED =
            "CONFIRMED";

    private static final String STATUS_CANCELLED =
            "CANCELLED";

    private static final String STATUS_REJECTED =
            "REJECTED";

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationServiceImpl(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository
    ) {
        this.registrationRepository =
                registrationRepository;

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
        return RegistrationMapper.toResponseDto(
                findRegistrationById(id)
        );
    }

    @Override
    public RegistrationResponseDto create(
            CreateRegistrationDto dto
    ) {
        if (registrationRepository
                .existsByEventIdAndParticipantId(
                        dto.getEventId(),
                        dto.getParticipantId()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya está registrado "
                            + "en este evento"
            );
        }

        EventEntity event =
                findEventById(dto.getEventId());

        validateEventForRegistration(event);

        UserEntity participant =
                findUserById(dto.getParticipantId());

        RegistrationEntity registration =
                new RegistrationEntity();

        registration.setEvent(event);
        registration.setParticipant(participant);
        registration.setStatus(STATUS_PENDING);

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

        EventEntity event = registration.getEvent();

        String previousStatus =
                registration.getStatus();

        String newStatus =
                dto.getStatus()
                        .trim()
                        .toUpperCase();

        validateStatus(newStatus);

        if (previousStatus.equals(newStatus)) {
            return RegistrationMapper.toResponseDto(
                    registration
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        boolean wasConfirmed =
                STATUS_CONFIRMED.equals(previousStatus);

        boolean willBeConfirmed =
                STATUS_CONFIRMED.equals(newStatus);

        if (!wasConfirmed && willBeConfirmed) {
            confirmRegistration(
                    registration,
                    event,
                    now
            );
        } else if (wasConfirmed && !willBeConfirmed) {
            releaseCapacity(event);

            registration.setConfirmedAt(null);

            if (STATUS_CANCELLED.equals(newStatus)) {
                registration.setCancelledAt(now);
            } else {
                registration.setCancelledAt(null);
            }
        } else {
            registration.setConfirmedAt(null);

            if (STATUS_CANCELLED.equals(newStatus)) {
                registration.setCancelledAt(now);
            } else {
                registration.setCancelledAt(null);
            }
        }

        registration.setStatus(newStatus);
        registration.setStatusUpdatedAt(now);

        /*
         * Las dos operaciones pertenecen a la misma transacción.
         * Si falla el guardado de una, Spring revierte ambas.
         */
        eventRepository.save(event);

        RegistrationEntity updated =
                registrationRepository.save(registration);

        return RegistrationMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        RegistrationEntity registration =
                findRegistrationById(id);

        if (STATUS_CONFIRMED.equals(
                registration.getStatus()
        )) {
            EventEntity event = registration.getEvent();

            releaseCapacity(event);
            eventRepository.save(event);
        }

        registrationRepository.delete(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponseDto> findByEvent(
            Long eventId
    ) {
        findEventById(eventId);

        return registrationRepository
                .findByEventId(eventId)
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

        return registrationRepository
                .findByParticipantId(participantId)
                .stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    private void validateEventForRegistration(
            EventEntity event
    ) {
        if (!"PUBLISHED".equalsIgnoreCase(
                event.getStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten inscripciones en "
                            + "eventos publicados"
            );
        }

        if (event.getEndDate() == null
                || !event.getEndDate()
                .isAfter(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar en un "
                            + "evento finalizado"
            );
        }

        if (event.getAvailableCapacity() == null
                || event.getAvailableCapacity() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El evento no tiene cupos disponibles"
            );
        }
    }

    private void confirmRegistration(
            RegistrationEntity registration,
            EventEntity event,
            OffsetDateTime now
    ) {
        if (event.getAvailableCapacity() == null
                || event.getAvailableCapacity() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El evento no tiene cupos disponibles"
            );
        }

        event.setAvailableCapacity(
                event.getAvailableCapacity() - 1
        );

        registration.setConfirmedAt(now);
        registration.setCancelledAt(null);
    }

    private void releaseCapacity(EventEntity event) {
        int available =
                event.getAvailableCapacity() == null
                        ? 0
                        : event.getAvailableCapacity();

        if (available < event.getCapacity()) {
            event.setAvailableCapacity(available + 1);
        }
    }

    private void validateStatus(String status) {
        boolean valid =
                STATUS_PENDING.equals(status)
                        || STATUS_CONFIRMED.equals(status)
                        || STATUS_CANCELLED.equals(status)
                        || STATUS_REJECTED.equals(status);

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de inscripción inválido"
            );
        }
    }

    private RegistrationEntity findRegistrationById(
            Long id
    ) {
        return registrationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inscripción no encontrada"
                        )
                );
    }

    private EventEntity findEventById(Long id) {
        return eventRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Evento no encontrado"
                        )
                );
    }

    private UserEntity findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );
    }
}