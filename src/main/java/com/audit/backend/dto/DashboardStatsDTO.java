package com.audit.backend.dto;

import com.audit.backend.entities.StatutMission;
import com.audit.backend.entities.GraviteAnomalie;
import com.audit.backend.entities.StatutAnomalie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalMissions;
    private Map<StatutMission, Long> missionsParStatut;
    private long totalAnomalies;
    private Map<GraviteAnomalie, Long> anomaliesParGravite;
    private Map<StatutAnomalie, Long> anomaliesParStatut;
    private long totalClients;
    private long totalDocuments;
}
