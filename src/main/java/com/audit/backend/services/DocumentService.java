package com.audit.backend.services;

import com.audit.backend.audit.Auditable;
import com.audit.backend.entities.Document;
import com.audit.backend.exception.BusinessException;
import com.audit.backend.repositories.DocumentRepository;
import com.audit.backend.security.UserDetailsImpl;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Auditable(action = "UPLOAD_DOCUMENT", entityType = "Document")
    public Document uploadDocument(MultipartFile file, Long missionId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Fichier invalide");
        }

        String fileName = FilenameUtils.getName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();

        if (!List.of("pdf", "xlsx", "docx").contains(extension)) {
            throw new BusinessException("Type de fichier non autorisé : " + extension);
        }

        try {
            Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            List<Document> existing = documentRepository.findByMissionIdOrderByVersionDesc(missionId);
            int nextVersion = existing.isEmpty() ? 1 : existing.get(0).getVersion() + 1;

            // désactive la version courante existante
            existing.forEach(doc -> {
                if (doc.isCurrentVersion()) {
                    doc.setCurrentVersion(false);
                    documentRepository.save(doc);
                }
            });

            String storageFileName = String.format("%d_%d_%s", missionId, nextVersion, fileName);
            Path targetLocation = uploadDirectory.resolve(storageFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Long uploadedBy = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
                uploadedBy = userDetails.getId();
            }

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setStoragePath(targetLocation.toString());
            document.setVersion(nextVersion);
            document.setMissionId(missionId);
            document.setUploadedBy(uploadedBy);
            document.setUploadedAt(LocalDateTime.now());
            document.setCurrentVersion(true);

            return documentRepository.save(document);

        } catch (IOException e) {
            throw new BusinessException("Impossible de stocker le fichier", e);
        }
    }

    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("Document introuvable"));

        try {
            Path path = Paths.get(document.getStoragePath());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException("Le fichier est introuvable sur le stockage");
            }
        } catch (Exception e) {
            throw new BusinessException("Erreur lors de la lecture du fichier", e);
        }
    }

    public List<Document> getDocumentsByMission(Long missionId) {
        return documentRepository.findByMissionIdOrderByVersionDesc(missionId);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("Document introuvable"));
    }

    @Auditable(action = "DELETE_DOCUMENT", entityType = "Document")
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("Document introuvable"));

        try {
            Path path = Paths.get(document.getStoragePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new BusinessException("Impossible de supprimer le fichier du stockage", e);
        }

        documentRepository.delete(document);
    }
}
