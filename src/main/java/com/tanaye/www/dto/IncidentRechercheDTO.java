package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutIncident;
import com.tanaye.www.enums.TypeIncident;

import java.time.LocalDateTime;

public record IncidentRechercheDTO(TypeIncident type, StatutIncident statut, Long declencheurId, Long colisId,
        LocalDateTime debut, LocalDateTime fin, Integer page, Integer size) {
}
