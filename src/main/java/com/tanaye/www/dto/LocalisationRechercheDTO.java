package com.tanaye.www.dto;

import java.time.LocalDateTime;

public record LocalisationRechercheDTO(Long utilisateurId,
        LocalDateTime debut,
        LocalDateTime fin,
        Double minLat,
        Double maxLat,
        Double minLng,
        Double maxLng,
        Integer page,
        Integer size) {
}
