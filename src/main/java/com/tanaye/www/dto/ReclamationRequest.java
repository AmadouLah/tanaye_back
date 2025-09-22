package com.tanaye.www.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReclamationRequest(
        @NotNull Long expediteurId,
        Long colisId,
        @NotBlank String objet,
        @NotBlank String description,
        @NotNull Boolean accepteCgu) {
}
