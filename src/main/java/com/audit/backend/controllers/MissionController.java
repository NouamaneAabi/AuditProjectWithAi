package com.audit.backend.controllers;

import com.audit.backend.entities.Mission;
import com.audit.backend.entities.StatutMission;
import com.audit.backend.services.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final com.audit.backend.services.WorkflowService workflowService;

    @GetMapping
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    @GetMapping("/{id}")
    public Mission getMissionById(@PathVariable Long id) {
        return missionService.getMissionById(id);
    }

    @PostMapping
    public Mission createMission(@RequestBody Mission mission) {
        return missionService.createMission(mission);
    }

    @PutMapping("/{id}")
    public Mission updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        return missionService.updateMission(id, mission);
    }

    @DeleteMapping("/{id}")
    public void deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
    }

    @GetMapping("/statut/{statut}")
    public List<Mission> getMissionsByStatut(@PathVariable StatutMission statut) {
        return missionService.getMissionsByStatut(statut);
    }

    @GetMapping("/client/{clientId}")
    public List<Mission> getMissionsByClient(@PathVariable Long clientId) {
        return missionService.getMissionsByClient(clientId);
    }

    @GetMapping("/auditeur/{auditeurId}")
    public List<Mission> getMissionsByAuditeur(@PathVariable Long auditeurId) {
        return missionService.getMissionsByAuditeur(auditeurId);
    }

    @PutMapping("/{id}/status")
    public Mission updateMissionStatus(@PathVariable Long id, @RequestParam StatutMission newStatus) {
        Mission mission = missionService.getMissionById(id);
        return workflowService.transition(mission, newStatus);
    }

    @GetMapping("/{id}/allowed-statuses")
    public List<StatutMission> getAllowedStatuses(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        return workflowService.getNextAllowedStatuses(mission.getStatut(), mission);
    }
}