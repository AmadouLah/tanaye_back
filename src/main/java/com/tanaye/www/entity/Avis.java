package com.tanaye.www.entity;

import java.time.LocalDateTime;

import com.tanaye.www.entity.base.EntiteAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "avis")
@Data
@EqualsAndHashCode(callSuper = true)
public class Avis extends EntiteAuditable {
    @Column(name = "note", nullable = false)
    private Integer note; // par ex. 1 à 5

    @Column(name = "commentaire", length = 1000)
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur; // qui laisse l'avis

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire; // qui reçoit l'avis

    @Column(name = "date_avis", nullable = false)
    private LocalDateTime dateAvis;
}

