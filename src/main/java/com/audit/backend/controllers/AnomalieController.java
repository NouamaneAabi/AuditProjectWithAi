package com.audit.backend.controllers;

import com.audit.backend.entities.Anomalie;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.GraviteAnomalie;
import com.audit.backend.services.AnomalieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
public class AnomalieController {

    private final AnomalieService anomalieService;

    @GetMapping
    public List<Anomalie> getAllAnomalies() {
        return anomalieService.getAllAnomalies();
    }

    @GetMapping("/{id}")
    public Anomalie getAnomalieById(@PathVariable Long id) {
        return anomalieService.getAnomalieById(id);
    }

    @PostMapping
    public Anomalie createAnomalie(@RequestBody Anomalie anomalie) {
        return anomalieService.createAnomalie(anomalie);
    }

    @PutMapping("/{id}")
    public Anomalie updateAnomalie(@PathVariable Long id, @RequestBody Anomalie anomalie) {
        return anomalieService.updateAnomalie(id, anomalie);
    }

    @DeleteMapping("/{id}")
    public void deleteAnomalie(@PathVariable Long id) {
        anomalieService.deleteAnomalie(id);
    }

    @GetMapping("/mission/{missionId}")
    public List<Anomalie> getAnomaliesByMission(@PathVariable Long missionId) {
        return anomalieService.getAnomaliesByMission(missionId);
    }

    @GetMapping("/statut/{statut}")
    public List<Anomalie> getAnomaliesByStatut(@PathVariable StatutAnomalie statut) {
        return anomalieService.getAnomaliesByStatut(statut);
    }

    @GetMapping("/gravite/{gravite}")
    public List<Anomalie> getAnomaliesByGravite(@PathVariable GraviteAnomalie gravite) {
        return anomalieService.getAnomaliesByGravite(gravite);
    }
}