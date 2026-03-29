package com.audit.backend.controllers;

import com.audit.backend.entities.Document;
import com.audit.backend.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload/{missionId}")
    public Document uploadDocument(@PathVariable Long missionId,
                                   @RequestParam("file") MultipartFile file) {
        return documentService.uploadDocument(file, missionId);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Document document = documentService.getDocumentById(documentId);
        Resource resource = documentService.downloadDocument(documentId);

        String contentType = document.getFileType() != null ? document.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/mission/{missionId}")
    public List<Document> getDocumentsByMission(@PathVariable Long missionId) {
        return documentService.getDocumentsByMission(missionId);
    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }
}
