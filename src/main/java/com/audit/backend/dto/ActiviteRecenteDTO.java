package com.audit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiviteRecenteDTO {
    private String action;
    private String performedByEmail;
    private String entityType;
    private LocalDateTime timestamp;
}
