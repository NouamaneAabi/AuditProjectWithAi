package com.audit.backend.repositories;

import com.audit.backend.entities.Anomalie;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.GraviteAnomalie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnomalieRepository extends JpaRepository<Anomalie, Long> {
    List<Anomalie> findByMissionId(Long missionId);
    List<Anomalie> findByCreateurId(Long createurId);
    List<Anomalie> findByStatut(StatutAnomalie statut);
    List<Anomalie> findByGravite(GraviteAnomalie gravite);
    List<Anomalie> findByMissionIdAndStatut(Long missionId, StatutAnomalie statut);
    List<Anomalie> findByMissionIdAndStatutNotIn(Long missionId, List<StatutAnomalie> statuts);
}