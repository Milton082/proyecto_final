package ec.edu.ups.academic_events_api.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.enums.UserStatus;

public interface UserRepository
        extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmailIgnoreCaseAndStatus(
            String email,
            UserStatus status);

    boolean existsByEmailIgnoreCase(String email);
}