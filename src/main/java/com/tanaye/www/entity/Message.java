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
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends EntiteAuditable {
    @Column(name = "contenu", nullable = false, length = 1000)
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @Column(name = "est_lu")
    private Boolean estLu = false;
}

