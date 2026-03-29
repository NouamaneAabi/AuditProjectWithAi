package com.audit.backend.repositories;

import com.audit.backend.entities.Mission;
import com.audit.backend.entities.StatutMission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByClientId(Long clientId);
    List<Mission> findByAuditeurId(Long auditeurId);
    List<Mission> findByStatut(StatutMission statut);
    boolean existsByCodeMission(String codeMission);
}