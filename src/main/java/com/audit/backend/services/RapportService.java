package com.audit.backend.services;

import com.audit.backend.entities.Rapport;
import com.audit.backend.repositories.RapportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;
    private final PdfGenerationService pdfGenerationService;

    public String generateRapport(Long missionId, Long userId) {
        return pdfGenerationService.generateRapport(missionId, userId);
    }

    public List<Rapport> getRapportsByMission(Long missionId) {
        return rapportRepository.findByMissionId(missionId);
    }

    public Rapport getRapportById(Long id) {
        return rapportRepository.findById(id).orElseThrow(() -> new RuntimeException("Rapport introuvable"));
    }

    public Resource downloadRapport(Long rapportId) {
        Rapport rapport = getRapportById(rapportId);
        try {
            Path path = Paths.get(rapport.getPdfPath());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Rapport introuvable");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de télécharger le rapport", e);
        }
    }
}
