package com.tanaye.www.dto;

import com.tanaye.www.enums.ModeTransport;
import com.tanaye.www.enums.StatutVoyage;
import com.tanaye.www.enums.TypeVoyage;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoyageRechercheDTO {
    private Long voyageurId;
    private Long villeDepartId;
    private Long villeArriveeId;
    private Long paysId;
    private String continent;
    private String query;
    private StatutVoyage statut;
    private TypeVoyage type;
    private ModeTransport modeTransport;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private BigDecimal poidsMin;
    private LocalDateTime dateDepartDebut;
    private LocalDateTime dateDepartFin;
    private LocalDateTime dateArriveeDebut;
    private LocalDateTime dateArriveeFin;
}
