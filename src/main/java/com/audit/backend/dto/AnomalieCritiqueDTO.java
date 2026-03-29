package com.audit.backend.dto;

import com.audit.backend.entities.GraviteAnomalie;
import com.audit.backend.entities.StatutAnomalie;
import com.audit.backend.entities.TypeAnomalie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalieCritiqueDTO {
    private Long id;
    private String titre;
    private String description;
    private TypeAnomalie type;
    private GraviteAnomalie gravite;
    private StatutAnomalie statut;
    private String recommandation;
}
