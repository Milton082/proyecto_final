package ec.edu.ups.academic_events_api.events.repositories;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findByIdAndDeletedFalse(Long id);
    List<EventEntity> findByDeletedFalse();
    List<EventEntity> findByCategoryIdAndDeletedFalse(
            Long categoryId
    );
    List<EventEntity> findByOrganizerIdAndDeletedFalse(
            Long organizerId
    );
    boolean existsByTitleIgnoreCaseAndDeletedFalse(
            String title
    );
    long countByDeletedFalse();
}