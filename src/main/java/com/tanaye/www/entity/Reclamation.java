package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "reclamations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Reclamation extends EntiteAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "colis_id")
    private Colis colis;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String objet;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 50)
    private String type;

    @Column(length = 50)
    private String statut;

    @Column(length = 1000)
    private String commentaire;

    @NotNull
    @Column(name = "acceptees_cgu", nullable = false)
    private Boolean accepteCgu; // confirmation d'avoir pris connaissance des CGU
}
