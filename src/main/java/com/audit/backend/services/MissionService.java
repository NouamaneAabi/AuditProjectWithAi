package com.audit.backend.services;

import com.audit.backend.entities.Mission;
import com.audit.backend.entities.StatutMission;
import com.audit.backend.entities.User;
import com.audit.backend.entities.Client;
import com.audit.backend.repositories.MissionRepository;
import com.audit.backend.repositories.UserRepository;
import com.audit.backend.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
    }

    public Mission createMission(Mission mission) {
        // Vérifier si le code mission existe déjà
        if (missionRepository.existsByCodeMission(mission.getCodeMission())) {
            throw new RuntimeException("Code mission déjà utilisé");
        }

        // Vérifier que le client existe
        if (mission.getClient() != null && mission.getClient().getId() != null) {
            Client client = clientRepository.findById(mission.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            mission.setClient(client);
        }

        // Vérifier que l'auditeur existe
        if (mission.getAuditeur() != null && mission.getAuditeur().getId() != null) {
            User auditeur = userRepository.findById(mission.getAuditeur().getId())
                    .orElseThrow(() -> new RuntimeException("Auditeur non trouvé"));
            mission.setAuditeur(auditeur);
        }

        return missionRepository.save(mission);
    }

    public Mission updateMission(Long id, Mission missionDetails) {
        Mission mission = getMissionById(id);

        mission.setCodeMission(missionDetails.getCodeMission());
        mission.setTitre(missionDetails.getTitre());
        mission.setDescription(missionDetails.getDescription());
        mission.setDateDebut(missionDetails.getDateDebut());
        mission.setDateFin(missionDetails.getDateFin());
        mission.setStatut(missionDetails.getStatut());

        // Mise à jour du client si nécessaire
        if (missionDetails.getClient() != null && missionDetails.getClient().getId() != null) {
            Client client = clientRepository.findById(missionDetails.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            mission.setClient(client);
        }

        // Mise à jour de l'auditeur si nécessaire
        if (missionDetails.getAuditeur() != null && missionDetails.getAuditeur().getId() != null) {
            User auditeur = userRepository.findById(missionDetails.getAuditeur().getId())
                    .orElseThrow(() -> new RuntimeException("Auditeur non trouvé"));
            mission.setAuditeur(auditeur);
        }

        return missionRepository.save(mission);
    }

    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    public List<Mission> getMissionsByStatut(StatutMission statut) {
        return missionRepository.findByStatut(statut);
    }

    public List<Mission> getMissionsByClient(Long clientId) {
        return missionRepository.findByClientId(clientId);
    }

    public List<Mission> getMissionsByAuditeur(Long auditeurId) {
        return missionRepository.findByAuditeurId(auditeurId);
    }
}