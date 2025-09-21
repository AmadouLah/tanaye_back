package com.tanaye.www.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.StatutVoyage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "voyages")
@Data
@EqualsAndHashCode(callSuper = true)
public class Voyage extends EntiteAuditable {
    @Column(name = "date_depart")
    private LocalDateTime dateDepart;

    @Column(name = "date_arrivee_estimee")
    private LocalDateTime dateArriveeEstimee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private Ville villeDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private Ville villeArrivee;

    @Column(name = "capacite_poids") // capacité maximale en kg
    private Double capacitePoids;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutVoyage statut = StatutVoyage.OUVERT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur voyageur; // l'utilisateur qui propose ce voyage

    @OneToMany(mappedBy = "voyage")
    private List<Colis> colisAttribues;
}

