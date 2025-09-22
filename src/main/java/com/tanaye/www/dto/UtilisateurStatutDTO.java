package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutUtilisateur;

public record UtilisateurStatutDTO(StatutUtilisateur statut, Boolean estVerifie) {
}
