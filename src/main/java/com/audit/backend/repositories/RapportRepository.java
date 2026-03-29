package com.audit.backend.repositories;

import com.audit.backend.entities.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByMissionId(Long missionId);
    Optional<Rapport> findTopByMissionIdOrderByDateGenerationDesc(Long missionId);
}