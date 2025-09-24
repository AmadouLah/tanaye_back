package com.tanaye.www.dto;

import com.tanaye.www.enums.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.tanaye.www.validation.StrongPassword;

@Data
public class RegisterRequest {
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est requis")
    private String email;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String telephone;

    @NotBlank(message = "Le mot de passe est requis")
    @StrongPassword
    private String motDePasse;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String confirmationMotDePasse;

    private RoleUtilisateur role = RoleUtilisateur.EXPEDITEUR;
}
