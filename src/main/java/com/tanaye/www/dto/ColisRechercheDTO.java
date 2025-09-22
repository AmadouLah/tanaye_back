package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutColis;
import com.tanaye.www.enums.TypeColis;

public record ColisRechercheDTO(Long villeDepartId,
        Long villeArriveeId,
        Long expediteurId,
        Long destinataireId,
        Long voyageId,
        StatutColis statut,
        TypeColis type,
        Integer page,
        Integer size) {
}
