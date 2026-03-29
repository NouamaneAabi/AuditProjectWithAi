package com.audit.backend.dto;

import com.audit.backend.entities.StatutMission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionRecenteDTO {
    private Long id;
    private String titre;
    private String clientNom;
    private StatutMission statut;
    private LocalDateTime dateDebut;
}
