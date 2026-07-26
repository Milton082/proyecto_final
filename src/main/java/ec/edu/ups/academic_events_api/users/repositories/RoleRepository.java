package ec.edu.ups.academic_events_api.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.academic_events_api.users.entities.RoleEntity;
import ec.edu.ups.academic_events_api.users.enums.RoleName;

public interface RoleRepository
        extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleName name);
}