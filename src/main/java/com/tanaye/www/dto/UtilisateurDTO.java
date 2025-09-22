package com.tanaye.www.dto;

import com.tanaye.www.entity.Utilisateur;

public record UtilisateurDTO(String nom, String prenom, String email, String telephone, String motDePasse) {
    public Utilisateur toEntity() {
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setTelephone(telephone);
        u.setMotDePasse(motDePasse);
        return u;
    }
}
