package ec.edu.ups.academic_events_api.sessions.services;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.sessions.dtos.CreateSessionDto;
import ec.edu.ups.academic_events_api.sessions.dtos.SessionResponseDto;
import ec.edu.ups.academic_events_api.sessions.dtos.UpdateSessionDto;
import ec.edu.ups.academic_events_api.sessions.entities.SessionEntity;
import ec.edu.ups.academic_events_api.sessions.mappers.SessionMapper;
import ec.edu.ups.academic_events_api.sessions.repositories.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;

    public SessionServiceImpl(
            SessionRepository sessionRepository,
            EventRepository eventRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> findAll() {

        return sessionRepository.findAll()
                .stream()
                .map(SessionMapper::toDto)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto findOne(Long id) {

        SessionEntity session = findSessionById(id);

        return SessionMapper.toDto(session);

    }

    @Override
    public SessionResponseDto create(CreateSessionDto dto) {

        validateDates(dto.getStartAt(), dto.getEndAt());

        EventEntity event = findEventById(dto.getEventId());

        SessionEntity session = SessionMapper.toEntity(dto);

        session.setEvent(event);

        SessionEntity savedSession = sessionRepository.save(session);

        return SessionMapper.toDto(savedSession);

    }

    @Override
    public SessionResponseDto update(Long id, UpdateSessionDto dto) {

        SessionEntity session = findSessionById(id);

        validateDates(dto.getStartAt(), dto.getEndAt());

        EventEntity event = findEventById(dto.getEventId());

        SessionMapper.update(session, dto);

        session.setEvent(event);

        SessionEntity updatedSession = sessionRepository.save(session);

        return SessionMapper.toDto(updatedSession);

    }

    @Override
    public void delete(Long id) {

        SessionEntity session = findSessionById(id);

        sessionRepository.delete(session);

    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> findByEvent(Long eventId) {

        findEventById(eventId);

        return sessionRepository.findByEventId(eventId)
                .stream()
                .map(SessionMapper::toDto)
                .toList();

    }

    private SessionEntity findSessionById(Long id) {

        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sesión no encontrada"
                        )
                );

    }

    private EventEntity findEventById(Long id) {

        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Evento no encontrado"
                        )
                );

    }

    private void validateDates(
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {

        if (startAt == null || endAt == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas son obligatorias"
            );

        }

        if (!endAt.isAfter(startAt)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de finalización debe ser posterior a la fecha de inicio"
            );

        }

    }

}