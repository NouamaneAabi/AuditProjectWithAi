package com.audit.backend.services;

import com.audit.backend.dto.*;
import com.audit.backend.entities.*;
import com.audit.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MissionRepository missionRepository;
    private final AnomalieRepository anomalieRepository;
    private final ClientRepository clientRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardStatsDTO getStats() {
        // Récupérer toutes les missions
        List<Mission> allMissions = missionRepository.findAll();
        long totalMissions = allMissions.size();

        // Missions par statut
        Map<StatutMission, Long> missionsParStatut = allMissions.stream()
                .collect(Collectors.groupingByConcurrent(Mission::getStatut, Collectors.counting()));

        // Récupérer toutes les anomalies
        List<Anomalie> allAnomalies = anomalieRepository.findAll();
        long totalAnomalies = allAnomalies.size();

        // Anomalies par gravité
        Map<GraviteAnomalie, Long> anomaliesParGravite = allAnomalies.stream()
                .collect(Collectors.groupingByConcurrent(Anomalie::getGravite, Collectors.counting()));

        // Anomalies par statut
        Map<StatutAnomalie, Long> anomaliesParStatut = allAnomalies.stream()
                .collect(Collectors.groupingByConcurrent(Anomalie::getStatut, Collectors.counting()));

        // Total clients et documents
        long totalClients = clientRepository.count();
        long totalDocuments = documentRepository.count();

        return new DashboardStatsDTO(
                totalMissions,
                missionsParStatut,
                totalAnomalies,
                anomaliesParGravite,
                anomaliesParStatut,
                totalClients,
                totalDocuments
        );
    }

    public List<MissionsEvolutionDTO> getMissionsEvolution() {
        List<Mission> allMissions = missionRepository.findAll();

        // Filtrer les missions des 6 derniers mois
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        Map<YearMonth, Long> evolutionByMonth = allMissions.stream()
                .filter(m -> m.getDateDebut() != null && m.getDateDebut().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingByConcurrent(
                        m -> YearMonth.from(m.getDateDebut()),
                        Collectors.counting()
                ));

        // Convertir en DTO avec format YYYY-MM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return evolutionByMonth.entrySet().stream()
                .map(entry -> new MissionsEvolutionDTO(entry.getKey().format(formatter), entry.getValue()))
                .sorted(Comparator.comparing(MissionsEvolutionDTO::getMonth))
                .collect(Collectors.toList());
    }

    public List<AnomalieCritiqueDTO> getAnomalieCritiques() {
        List<Anomalie> allAnomalies = anomalieRepository.findAll();

        return allAnomalies.stream()
                .filter(a -> a.getGravite() == GraviteAnomalie.CRITIQUE)
                .filter(a -> a.getStatut() != StatutAnomalie.RESOLUE && a.getStatut() != StatutAnomalie.FERMEE)
                .limit(10)
                .map(a -> new AnomalieCritiqueDTO(
                        a.getId(),
                        a.getTitre(),
                        a.getDescription(),
                        a.getType(),
                        a.getGravite(),
                        a.getStatut(),
                        a.getRecommandation()
                ))
                .collect(Collectors.toList());
    }

    public List<MissionRecenteDTO> getMissionsRecentes() {
        List<Mission> missions = missionRepository.findAll();

        return missions.stream()
                .sorted(Comparator.comparing(Mission::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(m -> new MissionRecenteDTO(
                        m.getId(),
                        m.getTitre(),
                        m.getClient() != null ? m.getClient().getNom() : "N/A",
                        m.getStatut(),
                        m.getDateDebut()
                ))
                .collect(Collectors.toList());
    }

    public List<ActiviteRecenteDTO> getActiviteRecente() {
        List<AuditLog> auditLogs = auditLogRepository.findAll();

        return auditLogs.stream()
                .sorted(Comparator.comparing(AuditLog::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(log -> new ActiviteRecenteDTO(
                        log.getAction(),
                        log.getPerformedByEmail(),
                        log.getEntityType(),
                        log.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
