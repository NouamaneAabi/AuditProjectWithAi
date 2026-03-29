package com.audit.backend.services;

import com.audit.backend.entities.Anomalie;
import com.audit.backend.entities.Client;
import com.audit.backend.entities.Mission;
import com.audit.backend.entities.Rapport;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.StatutMission;
import com.audit.backend.exception.BusinessException;
import com.audit.backend.repositories.AnomalieRepository;
import com.audit.backend.repositories.ClientRepository;
import com.audit.backend.repositories.MissionRepository;
import com.audit.backend.repositories.RapportRepository;
import com.audit.backend.audit.Auditable;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PdfGenerationService {

    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final AnomalieRepository anomalieRepository;
    private final RapportRepository rapportRepository;

    @Value("${file.report-dir:reports}")
    private String reportDir;

    public PdfGenerationService(MissionRepository missionRepository,
                                ClientRepository clientRepository,
                                AnomalieRepository anomalieRepository,
                                RapportRepository rapportRepository) {
        this.missionRepository = missionRepository;
        this.clientRepository = clientRepository;
        this.anomalieRepository = anomalieRepository;
        this.rapportRepository = rapportRepository;
    }

    @Auditable(action = "GENERATE_REPORT", entityType = "Rapport")
    public String generateRapport(Long missionId, Long userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BusinessException("Mission introuvable"));

        Client client = mission.getClient() != null && mission.getClient().getId() != null
                ? clientRepository.findById(mission.getClient().getId())
                .orElseThrow(() -> new BusinessException("Client introuvable"))
                : null;

        List<Anomalie> anomalies = anomalieRepository.findByMissionId(missionId);

        try {
            Path reportDirectory = Paths.get(reportDir).toAbsolutePath().normalize();
            if (!Files.exists(reportDirectory)) {
                Files.createDirectories(reportDirectory);
            }

            String filename = String.format("rapport_mission_%d_%d.pdf", missionId, System.currentTimeMillis());
            Path pdfPath = reportDirectory.resolve(filename);

            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath.toFile()));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Rapport d'audit - Mission #" + missionId).setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Informations mission").setBold());
            document.add(new Paragraph("Code: " + mission.getCodeMission()));
            document.add(new Paragraph("Titre: " + mission.getTitre()));
            document.add(new Paragraph("Description: " + mission.getDescription()));
            document.add(new Paragraph("Statut: " + (mission.getStatut() != null ? mission.getStatut() : "n/a")));
            if (mission.getDateDebut() != null) {
                document.add(new Paragraph("Date début: " + mission.getDateDebut()));
            }
            if (mission.getDateFin() != null) {
                document.add(new Paragraph("Date fin: " + mission.getDateFin()));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Informations client").setBold());
            if (client != null) {
                document.add(new Paragraph("Nom: " + client.getNom()));
                document.add(new Paragraph("Secteur: " + client.getSecteurActivite()));
                document.add(new Paragraph("Contact: " + (client.getEmail() != null ? client.getEmail() : "") + " " + (client.getTelephone() != null ? client.getTelephone() : "")));
            } else {
                document.add(new Paragraph("Aucun client lié"));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Liste des anomalies").setBold());

            Table table = new Table(new float[]{3, 5, 2, 2, 2, 5});
            table.addHeaderCell(new Cell().add(new Paragraph("Titre")));
            table.addHeaderCell(new Cell().add(new Paragraph("Description")));
            table.addHeaderCell(new Cell().add(new Paragraph("Type")));
            table.addHeaderCell(new Cell().add(new Paragraph("Gravité")));
            table.addHeaderCell(new Cell().add(new Paragraph("Statut")));
            table.addHeaderCell(new Cell().add(new Paragraph("Recommandation")));

            for (Anomalie anomalie : anomalies) {
                table.addCell(new Cell().add(new Paragraph(anomalie.getTitre())));
                table.addCell(new Cell().add(new Paragraph(anomalie.getDescription())));
                table.addCell(new Cell().add(new Paragraph(anomalie.getType() != null ? anomalie.getType().name() : "")));
                table.addCell(new Cell().add(new Paragraph(anomalie.getGravite() != null ? anomalie.getGravite().name() : "")));
                table.addCell(new Cell().add(new Paragraph(anomalie.getStatut() != null ? anomalie.getStatut().name() : "")));
                table.addCell(new Cell().add(new Paragraph(anomalie.getRecommandation())));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Généré le : " + LocalDateTime.now()));

            document.close();

            Rapport rapport = new Rapport();
            rapport.setMissionId(missionId);
            rapport.setDateGeneration(LocalDateTime.now());
            rapport.setPdfPath(pdfPath.toString());
            rapport.setGenerePar(userId);
            rapport.setValide(false);

            rapportRepository.save(rapport);

            return pdfPath.toString();

        } catch (IOException e) {
            throw new BusinessException("Erreur lors de la génération du PDF", e);
        }
    }
}