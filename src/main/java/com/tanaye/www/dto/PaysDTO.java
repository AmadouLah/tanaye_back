package com.tanaye.www.dto;

import com.tanaye.www.entity.Pays;

public record PaysDTO(String nom, String codeIso) {
    public Pays toEntity() {
        Pays p = new Pays();
        p.setNom(nom);
        p.setCodeIso(codeIso);
        return p;
    }
}
