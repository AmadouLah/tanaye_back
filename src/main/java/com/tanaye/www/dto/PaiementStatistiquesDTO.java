package com.tanaye.www.dto;

import com.tanaye.www.enums.ModePaiement;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaiementStatistiquesDTO {
    private ModePaiement mode;
    private long nombrePaiements;
    private BigDecimal montantTotal;
    private BigDecimal montantMoyen;
}
