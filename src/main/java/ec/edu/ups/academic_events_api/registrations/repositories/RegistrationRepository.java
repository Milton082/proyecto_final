package ec.edu.ups.academic_events_api.registrations.repositories;

import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    List<RegistrationEntity> findByEventId(Long eventId);
    List<RegistrationEntity> findByParticipantId(Long participantId);
    Optional<RegistrationEntity> findByEventIdAndParticipantId(
            Long eventId,
            Long participantId
    );

    boolean existsByEventIdAndParticipantId(
            Long eventId,
            Long participantId
    );

    long countByStatus(String status);

    long countByRegisteredAtBetween(
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );

    long countByStatusAndRegisteredAtBetween(
            String status,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );
}