package com.audit.backend.controllers;

import com.audit.backend.entities.Rapport;
import com.audit.backend.dto.RapportDTO;
import com.audit.backend.services.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @PostMapping("/generate/{missionId}")
    public String generateRapport(@PathVariable Long missionId, @RequestParam Long userId) {
        return rapportService.generateRapport(missionId, userId);
    }

    @GetMapping("/download/{rapportId}")
    public ResponseEntity<Resource> downloadRapport(@PathVariable Long rapportId) {
        Resource resource = rapportService.downloadRapport(rapportId);
        Rapport rapport = rapportService.getRapportById(rapportId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rapport_" + rapportId + ".pdf\"")
                .body(resource);
    }

    @GetMapping("/mission/{missionId}")
    public List<RapportDTO> getRapportsByMission(@PathVariable Long missionId) {
        return rapportService.getRapportsByMission(missionId).stream()
                .map(RapportDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
