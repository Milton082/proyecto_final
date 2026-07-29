package ec.edu.ups.academic_events_api.registrations.repositories;

import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @EntityGraph(attributePaths = {
            "participant",
            "event",
            "event.organizer"
    })
    List<RegistrationEntity> findByEventIdOrderByRegisteredAtAsc(
            Long eventId
    );

    @Query("""
            SELECT r
            FROM RegistrationEntity r
            JOIN FETCH r.participant
            JOIN FETCH r.event
            WHERE r.id = :registrationId
            """)
    Optional<RegistrationEntity> findByIdWithParticipantAndEvent(
            @Param("registrationId") Long registrationId
    );
}