package com.audit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storagePath;
    private Integer version;
    private Long missionId;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private boolean currentVersion;
}
