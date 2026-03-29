package com.audit.backend.services;

import com.audit.backend.entities.Anomalie;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.GraviteAnomalie;
import com.audit.backend.entities.Mission;
import com.audit.backend.entities.User;
import com.audit.backend.repositories.AnomalieRepository;
import com.audit.backend.repositories.MissionRepository;
import com.audit.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalieService {

    private final AnomalieRepository anomalieRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public List<Anomalie> getAllAnomalies() {
        return anomalieRepository.findAll();
    }

    public Anomalie getAnomalieById(Long id) {
        return anomalieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomalie non trouvée"));
    }

    public Anomalie createAnomalie(Anomalie anomalie) {
        // Vérifier que la mission existe
        if (anomalie.getMission() != null && anomalie.getMission().getId() != null) {
            Mission mission = missionRepository.findById(anomalie.getMission().getId())
                    .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
            anomalie.setMission(mission);
        }

        // Vérifier que le créateur existe
        if (anomalie.getCreateur() != null && anomalie.getCreateur().getId() != null) {
            User createur = userRepository.findById(anomalie.getCreateur().getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            anomalie.setCreateur(createur);
        }

        return anomalieRepository.save(anomalie);
    }

    public Anomalie updateAnomalie(Long id, Anomalie anomalieDetails) {
        Anomalie anomalie = getAnomalieById(id);

        anomalie.setTitre(anomalieDetails.getTitre());
        anomalie.setDescription(anomalieDetails.getDescription());
        anomalie.setType(anomalieDetails.getType());
        anomalie.setGravite(anomalieDetails.getGravite());
        anomalie.setImpact(anomalieDetails.getImpact());
        anomalie.setRecommandation(anomalieDetails.getRecommandation());
        anomalie.setStatut(anomalieDetails.getStatut());

        if (anomalieDetails.getStatut() == StatutAnomalie.RESOLUE && anomalie.getDateResolution() == null) {
            anomalie.setDateResolution(LocalDateTime.now());
        }

        return anomalieRepository.save(anomalie);
    }

    public void deleteAnomalie(Long id) {
        anomalieRepository.deleteById(id);
    }

    public List<Anomalie> getAnomaliesByMission(Long missionId) {
        return anomalieRepository.findByMissionId(missionId);
    }

    public List<Anomalie> getAnomaliesByStatut(StatutAnomalie statut) {
        return anomalieRepository.findByStatut(statut);
    }

    public List<Anomalie> getAnomaliesByGravite(GraviteAnomalie gravite) {
        return anomalieRepository.findByGravite(gravite);
    }
}