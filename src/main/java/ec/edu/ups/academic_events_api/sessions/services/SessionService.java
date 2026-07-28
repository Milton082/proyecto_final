package ec.edu.ups.academic_events_api.sessions.services;

import ec.edu.ups.academic_events_api.sessions.dtos.CreateSessionDto;
import ec.edu.ups.academic_events_api.sessions.dtos.SessionResponseDto;
import ec.edu.ups.academic_events_api.sessions.dtos.UpdateSessionDto;
import java.util.List;

public interface SessionService {

    List<SessionResponseDto> findAll();
    SessionResponseDto findOne(Long id);
    SessionResponseDto create(CreateSessionDto dto);
    SessionResponseDto update(Long id, UpdateSessionDto dto);
    void delete(Long id);
    List<SessionResponseDto> findByEvent(Long eventId);

}