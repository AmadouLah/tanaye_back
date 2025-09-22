package com.tanaye.www.dto;

import com.tanaye.www.enums.ModePaiement;
import com.tanaye.www.enums.StatutPaiement;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaiementRechercheDTO {
    private Long payeurId;
    private Long colisId;
    private ModePaiement mode;
    private StatutPaiement statut;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String referenceExterne;
}
