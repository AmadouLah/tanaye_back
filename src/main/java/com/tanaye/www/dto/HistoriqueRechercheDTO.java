package com.tanaye.www.dto;

import java.time.LocalDateTime;

public record HistoriqueRechercheDTO(Long utilisateurId, String action, LocalDateTime debut, LocalDateTime fin,
        Integer page, Integer size) {
}
