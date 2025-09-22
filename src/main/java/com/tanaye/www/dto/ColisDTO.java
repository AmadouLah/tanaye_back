package com.tanaye.www.dto;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.enums.TypeColis;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ColisDTO(Long expediteurId,
        Long destinataireId,
        Long villeDepartId,
        Long villeArriveeId,
        TypeColis type,
        Double poids,
        Double largeur,
        Double longueur,
        Double hauteur,
        BigDecimal prix,
        LocalDate dateEnvoiSouhaitee) {
    public Colis toEntity() {
        Colis c = new Colis();
        c.setType(type);
        c.setPoids(poids);
        c.setLargeur(largeur);
        c.setLongueur(longueur);
        c.setHauteur(hauteur);
        c.setPrix(prix);
        c.setDateEnvoiSouhaitee(dateEnvoiSouhaitee);
        return c;
    }
}
