package com.tanaye.www.dto;

import com.tanaye.www.entity.Voyage;

import java.time.LocalDateTime;

public record VoyageDTO(Long voyageurId, Long villeDepartId, Long villeArriveeId,
        LocalDateTime dateDepart, LocalDateTime dateArriveeEstimee,
        Double capacitePoids) {
    public Voyage toEntity() {
        Voyage v = new Voyage();
        v.setDateDepart(dateDepart);
        v.setDateArriveeEstimee(dateArriveeEstimee);
        v.setCapacitePoids(capacitePoids);
        return v;
    }
}
