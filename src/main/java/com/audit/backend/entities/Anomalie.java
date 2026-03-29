package com.audit.backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomalies")
@Data
@NoArgsConstructor
public class Anomalie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TypeAnomalie type;

    @Enumerated(EnumType.STRING)
    private GraviteAnomalie gravite;

    private String impact;

    private String recommandation;

    @Enumerated(EnumType.STRING)
    private StatutAnomalie statut;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    private LocalDateTime dateDetection;
    private LocalDateTime dateResolution;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateDetection == null) {
            dateDetection = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}