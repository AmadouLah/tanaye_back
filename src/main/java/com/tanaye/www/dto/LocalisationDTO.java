package com.tanaye.www.dto;

import com.tanaye.www.entity.Localisation;

public record LocalisationDTO(Long utilisateurId, Double latitude, Double longitude) {
    public Localisation toEntity() {
        Localisation l = new Localisation();
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        return l;
    }
}
