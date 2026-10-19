package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.model.AuditEvent;
import de.kirillmrotzek.legalflow.repository.AuditEventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    private void save(
            String username,
            AuditAction action,
            String entityType,
            Long entityId
    ) {
        AuditEvent event = new AuditEvent();

        event.setUsername(username);
        event.setAction(action);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setTimestamp(LocalDateTime.now());

        auditEventRepository.save(event);
    }

    public void log(
            AuditAction action,
            String entityType,
            Long entityId
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        save(
                authentication.getName(),
                action,
                entityType,
                entityId
        );
    }

    public void logSystem(
            AuditAction action,
            String entityType,
            Long entityId
    ) {
        save(
                "SYSTEM",
                action,
                entityType,
                entityId
        );
    }
}
