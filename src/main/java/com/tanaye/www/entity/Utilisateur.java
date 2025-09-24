package com.tanaye.www.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tanaye.www.validation.StrongPassword;

@Entity
@Table(name = "utilisateurs")
@Data
@EqualsAndHashCode(callSuper = true)
public class Utilisateur extends EntiteAuditable {
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String prenom;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telephone;

    @Column(name = "mot_de_passe", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @StrongPassword
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleUtilisateur role = RoleUtilisateur.EXPEDITEUR;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutUtilisateur statut = StatutUtilisateur.ACTIF;

    @Column(name = "est_verifie")
    private Boolean estVerifie = false; // vérification d'identité

    // Relations
    @OneToMany(mappedBy = "voyageur")
    private List<Voyage> voyages; // trajets proposés (si l'utilisateur est voyageur)

    @OneToMany(mappedBy = "expediteur")
    private List<Colis> colisEnvoyes; // envois créés (si expéditeur)

    @OneToMany(mappedBy = "destinataire")
    private List<Colis> colisReçus; // en tant que destinataire

    @OneToMany(mappedBy = "auteur")
    private List<Message> messagesEnvoyes;

    @OneToMany(mappedBy = "destinataire")
    private List<Message> messagesRecus;

    @OneToMany(mappedBy = "auteur")
    private List<Avis> avisDonnes; // avis laissés par cet utilisateur

    @OneToMany(mappedBy = "destinataire")
    private List<Avis> avisRecus; // avis reçus par cet utilisateur
}
