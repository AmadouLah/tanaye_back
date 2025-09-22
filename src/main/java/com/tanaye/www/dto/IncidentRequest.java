package com.tanaye.www.dto;

import com.tanaye.www.enums.TypeIncident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncidentRequest(
        @NotNull Long declencheurId,
        Long colisId,
        @NotNull TypeIncident type,
        @NotBlank String description) {
}
