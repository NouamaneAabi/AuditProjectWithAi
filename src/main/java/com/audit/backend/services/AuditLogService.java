package com.audit.backend.services;

import com.audit.backend.entities.AuditLog;
import com.audit.backend.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog logAction(String action,
                              String entityType,
                              Long entityId,
                              String details,
                              Long performedBy,
                              String performedByEmail,
                              String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setPerformedBy(performedBy);
        auditLog.setPerformedByEmail(performedByEmail);
        auditLog.setIpAddress(ipAddress);
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }
}
