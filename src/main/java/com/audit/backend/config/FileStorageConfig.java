package com.audit.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.report-dir:reports}")
    private String reportDir;

    @PostConstruct
    public void init() {
        createDirectory(uploadDir);
        createDirectory(reportDir);
    }

    private void createDirectory(String dir) {
        Path path = Paths.get(dir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage des fichiers : " + path, e);
        }
    }
}
