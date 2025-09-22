package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutReclamation;
import com.tanaye.www.enums.TypeReclamation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReclamationRechercheDTO {
    private Long expediteurId;
    private Long colisId;
    private String query;
    private TypeReclamation type;
    private StatutReclamation statut;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
