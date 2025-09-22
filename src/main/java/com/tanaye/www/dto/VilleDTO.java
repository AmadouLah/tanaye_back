package com.tanaye.www.dto;

import com.tanaye.www.entity.Ville;

public record VilleDTO(Long regionId, String nom) {
    public Ville toEntity() {
        Ville v = new Ville();
        v.setNom(nom);
        return v;
    }
}
