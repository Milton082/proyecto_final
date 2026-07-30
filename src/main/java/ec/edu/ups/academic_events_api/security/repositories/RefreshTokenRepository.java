package ec.edu.ups.academic_events_api.security.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.academic_events_api.security.entities.RefreshTokenEntity;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenId(UUID tokenId);

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    boolean existsByTokenIdAndRevokedAtIsNull(UUID tokenId);

    long deleteByExpiresAtBefore(OffsetDateTime dateTime);
}