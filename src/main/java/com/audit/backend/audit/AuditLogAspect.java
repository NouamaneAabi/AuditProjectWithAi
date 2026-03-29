package com.audit.backend.audit;

import com.audit.backend.entities.AuditLog;
import com.audit.backend.security.UserDetailsImpl;
import com.audit.backend.services.AuditLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.audit.backend.audit.Auditable)")
    public Object aroundAuditable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable annotation = method.getAnnotation(Auditable.class);

        Object result = joinPoint.proceed();

        try {
            String action = annotation.action();
            String entityType = annotation.entityType();
            String ipAddress = resolveIpAddress();

            Long performedBy = null;
            String performedByEmail = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                performedBy = userDetails.getId();
                performedByEmail = userDetails.getEmail();
            }

            Long entityId = resolveEntityId(annotation, signature, joinPoint.getArgs(), result);

            String details = buildDetails(annotation, method, joinPoint.getArgs(), result);

            AuditLog auditLog = auditLogService.logAction(action, entityType, entityId, details, performedBy,
                    performedByEmail, ipAddress);

        } catch (Exception e) {
            // ne jamais bloquer la logique applicative si l'audit échoue
        }

        return result;
    }

    private String resolveIpAddress() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                return forwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "UNKNOWN";
    }

    private Long resolveEntityId(Auditable annotation, MethodSignature signature, Object[] args, Object result) {
        String entityIdArg = annotation.entityIdArg();

        if (!entityIdArg.isBlank()) {
            String[] paramNames = signature.getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    if (entityIdArg.equals(paramNames[i]) && args[i] instanceof Number) {
                        return ((Number) args[i]).longValue();
                    }
                }
            }
        }

        if (result != null) {
            try {
                Method getId = result.getClass().getMethod("getId");
                Object id = getId.invoke(result);
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String buildDetails(Auditable annotation, Method method, Object[] args, Object result) {
        if (!annotation.includeResult()) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(
                    new AuditDetails(method, args, result));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private record AuditDetails(String method, Object[] args, Object result) {
        AuditDetails(Method method, Object[] args, Object result) {
            this(method.getName(), args, result);
        }
    }
}
