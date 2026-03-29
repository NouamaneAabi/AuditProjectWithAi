package com.audit.backend.services;

import com.audit.backend.audit.Auditable;
import com.audit.backend.entities.Anomalie;
import com.audit.backend.entities.Mission;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.StatutMission;
import com.audit.backend.exception.BusinessException;
import com.audit.backend.repositories.AnomalieRepository;
import com.audit.backend.repositories.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final MissionRepository missionRepository;
    private final AnomalieRepository anomalieRepository;

    public boolean canTransition(StatutMission current, StatutMission target, Mission mission) {
        if (current == null || target == null) {
            return false;
        }

        if (current == target) {
            return false;
        }

        // interdiction de revenir en arrière
        if (current.ordinal() > target.ordinal()) {
            return false;
        }

        // pas de saut d'étapes
        if (target.ordinal() != current.ordinal() + 1) {
            return false;
        }

        return switch (current) {
            case CREE -> target == StatutMission.EN_ANALYSE;
            case EN_ANALYSE -> target == StatutMission.RAPPORT_EN_COURS && anomaliesResolvables(mission);
            case RAPPORT_EN_COURS -> target == StatutMission.VALIDEE && checkRapportExiste(mission.getId());
            case VALIDEE -> target == StatutMission.CLOTUREE;
            default -> false;
        };
    }

    @Auditable(action = "UPDATE_MISSION_STATUS", entityType = "Mission")
    public Mission transition(Mission mission, StatutMission newStatus) {
        if (mission == null) {
            throw new BusinessException("Mission introuvable");
        }

        StatutMission current = mission.getStatut();

        if (!canTransition(current, newStatus, mission)) {
            throw new BusinessException(String.format("Transition de %s à %s non autorisée", current, newStatus));
        }

        mission.setStatut(newStatus);
        return missionRepository.save(mission);
    }

    public List<StatutMission> getNextAllowedStatuses(StatutMission current, Mission mission) {
        List<StatutMission> allowed = new ArrayList<>();

        if (current == null) {
            return allowed;
        }

        for (StatutMission candidate : StatutMission.values()) {
            if (canTransition(current, candidate, mission)) {
                allowed.add(candidate);
            }
        }

        return allowed;
    }

    private boolean anomaliesResolvables(Mission mission) {
        if (mission == null || mission.getId() == null) {
            return false;
        }
        List<Anomalie> nonResolves = anomalieRepository.findByMissionIdAndStatutNotIn(mission.getId(),
                Arrays.asList(StatutAnomalie.RESOLUE, StatutAnomalie.FERMEE));
        return nonResolves.isEmpty();
    }

    public boolean checkRapportExiste(Long missionId) {
        // TODO: implémenter avec entité Rapport une fois créée.
        return false;
    }
}