package com.tanaye.www.dto;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.RoleUtilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long utilisateurId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private RoleUtilisateur role;
    private Boolean estVerifie;
    
    public static AuthResponse from(Utilisateur utilisateur, String token) {
        return AuthResponse.builder()
                .token(token)
                .utilisateurId(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .role(utilisateur.getRole())
                .estVerifie(utilisateur.getEstVerifie())
                .build();
    }
}
