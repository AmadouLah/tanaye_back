package com.tanaye.www.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecuDTO(Long id,
        String numero,
        Long paiementId,
        Long payeurId,
        Long beneficiaireId,
        BigDecimal montant,
        String mode,
        String referenceExterne,
        LocalDateTime dateEmission) {
}
