package com.audit.backend.controllers;

import com.audit.backend.dto.*;
import com.audit.backend.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_CABINET')")
    public DashboardStatsDTO getStats() {
        return dashboardService.getStats();
    }

    @GetMapping("/missions-evolution")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_CABINET')")
    public List<MissionsEvolutionDTO> getMissionsEvolution() {
        return dashboardService.getMissionsEvolution();
    }

    @GetMapping("/anomalies-critiques")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_CABINET')")
    public List<AnomalieCritiqueDTO> getAnomalieCritiques() {
        return dashboardService.getAnomalieCritiques();
    }

    @GetMapping("/missions-recentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_CABINET')")
    public List<MissionRecenteDTO> getMissionsRecentes() {
        return dashboardService.getMissionsRecentes();
    }

    @GetMapping("/activite-recente")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE_CABINET')")
    public List<ActiviteRecenteDTO> getActiviteRecente() {
        return dashboardService.getActiviteRecente();
    }
}
