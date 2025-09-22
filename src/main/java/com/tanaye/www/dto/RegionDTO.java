package com.tanaye.www.dto;

import com.tanaye.www.entity.Region;

public record RegionDTO(Long paysId, String nom) {
    public Region toEntity() {
        Region r = new Region();
        r.setNom(nom);
        return r;
    }
}
