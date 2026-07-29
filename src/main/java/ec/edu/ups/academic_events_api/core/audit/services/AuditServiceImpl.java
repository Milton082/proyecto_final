package ec.edu.ups.academic_events_api.core.audit.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ec.edu.ups.academic_events_api.core.audit.entities.AuditLogEntity;
import ec.edu.ups.academic_events_api.core.audit.enums.AuditAction;
import ec.edu.ups.academic_events_api.core.audit.enums.AuditResult;
import ec.edu.ups.academic_events_api.core.audit.repositories.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.json.JsonMapper;

@Service
public class AuditServiceImpl implements AuditService {

    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    private final AuditLogRepository auditLogRepository;
    private final JsonMapper jsonMapper;

    public AuditServiceImpl(
            AuditLogRepository auditLogRepository,
            JsonMapper jsonMapper) {
        this.auditLogRepository = auditLogRepository;
        this.jsonMapper = jsonMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void register(
            Long actorId,
            AuditAction action,
            String resourceType,
            Long resourceId,
            Object previousValue,
            Object newValue,
            AuditResult result) {
        RequestInformation requestInformation = obtainRequestInformation();

        AuditLogEntity entity = new AuditLogEntity();

        entity.setActorId(actorId);
        entity.setAction(action.name());
        entity.setResourceType(resourceType);
        entity.setResourceId(resourceId);
        entity.setPreviousValue(toJsonMap(previousValue));
        entity.setNewValue(toJsonMap(newValue));
        entity.setResult(result);
        entity.setIpAddress(
                requestInformation.ipAddress());
        entity.setHttpMethod(
                requestInformation.httpMethod());
        entity.setEndpoint(
                requestInformation.endpoint());
        entity.setCorrelationId(
                requestInformation.correlationId());

        auditLogRepository.save(entity);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toJsonMap(
            Object value) {
        if (value == null) {
            return null;
        }

        return jsonMapper.convertValue(
                value,
                Map.class);
    }

    private RequestInformation obtainRequestInformation() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();

        if (attributes == null) {
            return new RequestInformation(
                    null,
                    null,
                    null,
                    UUID.randomUUID().toString());
        }

        HttpServletRequest request = attributes.getRequest();

        return new RequestInformation(
                extractClientIp(request),
                request.getMethod(),
                request.getRequestURI(),
                extractCorrelationId(request));
    }

    private String extractClientIp(
            HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }

    private String extractCorrelationId(
            HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_HEADER);

        if (correlationId == null
                || correlationId.isBlank()
                || correlationId.length() > 36) {

            return UUID.randomUUID().toString();
        }

        return correlationId.trim();
    }

    private record RequestInformation(
            String ipAddress,
            String httpMethod,
            String endpoint,
            String correlationId) {
    }
}