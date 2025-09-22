package com.tanaye.www.dto;

import com.tanaye.www.enums.ModePaiement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaiementRequest(
        @NotNull Long payeurId,
        Long colisId,
        @NotNull ModePaiement mode,
        @NotNull @Min(0) BigDecimal montant) {
}
