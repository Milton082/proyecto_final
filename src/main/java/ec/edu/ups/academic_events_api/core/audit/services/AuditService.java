package ec.edu.ups.academic_events_api.core.audit.services;

import ec.edu.ups.academic_events_api.core.audit.enums.AuditAction;
import ec.edu.ups.academic_events_api.core.audit.enums.AuditResult;

public interface AuditService {

    void register(
            Long actorId,
            AuditAction action,
            String resourceType,
            Long resourceId,
            Object previousValue,
            Object newValue,
            AuditResult result);
}