package ec.edu.ups.academic_events_api.core.audit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.academic_events_api.core.audit.entities.AuditLogEntity;

public interface AuditLogRepository
        extends JpaRepository<AuditLogEntity, Long> {
}