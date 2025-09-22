package com.tanaye.www.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "L'identifiant est requis")
    private String identifiant; // email ou téléphone

    @NotBlank(message = "Le mot de passe est requis")
    private String motDePasse;
}
