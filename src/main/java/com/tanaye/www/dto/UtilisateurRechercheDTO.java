package com.tanaye.www.dto;

import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;

public record UtilisateurRechercheDTO(String terme,
        RoleUtilisateur role,
        StatutUtilisateur statut,
        Boolean verifie,
        Integer page,
        Integer size) {
}
