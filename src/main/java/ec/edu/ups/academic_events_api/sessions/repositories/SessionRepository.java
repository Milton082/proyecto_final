package ec.edu.ups.academic_events_api.sessions.repositories;

import ec.edu.ups.academic_events_api.sessions.entities.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<SessionEntity,Long>{

    List<SessionEntity> findByEventId(Long eventId);

}