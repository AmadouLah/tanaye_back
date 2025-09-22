package com.tanaye.www.dto;

import java.util.Map;

public record AvisStatsDTO(Long utilisateurId, Double moyenne, Long total, Map<Integer, Long> distribution) {
}
