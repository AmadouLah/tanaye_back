package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutRecu;
import com.tanaye.www.enums.TypeRecu;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecuRechercheDTO {
    private Long payeurId;
    private Long paiementId;
    private String query;
    private TypeRecu type;
    private StatutRecu statut;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer annee;
}
