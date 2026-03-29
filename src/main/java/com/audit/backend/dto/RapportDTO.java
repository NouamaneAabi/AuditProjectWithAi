package com.audit.backend.dto;

import com.audit.backend.entities.Rapport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RapportDTO {
    private Long id;
    private Long missionId;
    private LocalDateTime dateGeneration;
    private String pdfPath;
    private Long generePar;
    private boolean valide;

    public static RapportDTO fromEntity(Rapport rapport) {
        if (rapport == null) return null;
        return new RapportDTO(
                rapport.getId(),
                rapport.getMissionId(),
                rapport.getDateGeneration(),
                rapport.getPdfPath(),
                rapport.getGenerePar(),
                rapport.isValide());
    }
}
