package com.tanaye.www.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.StatutColis;
import com.tanaye.www.enums.TypeColis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "colis")
@Data
@EqualsAndHashCode(callSuper = true)
public class Colis extends EntiteAuditable {
    @Enumerated(EnumType.STRING)
    @Column(name = "type_colis")
    private TypeColis type;

    @Column(name = "poids")
    private Double poids;

    @Column(name = "largeur")
    private Double largeur;

    @Column(name = "longueur")
    private Double longueur;

    @Column(name = "hauteur")
    private Double hauteur;

    @Column(name = "prix")
    private BigDecimal prix; // tarif négocié ou défini pour ce colis

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutColis statut = StatutColis.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire; // désigné par l'expéditeur

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private Ville villeDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private Ville villeArrivee;

    @Column(name = "date_envoi_souhaitee")
    private LocalDate dateEnvoiSouhaitee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id")
    private Voyage voyage; // voyage sélectionné pour transporter le colis
}
