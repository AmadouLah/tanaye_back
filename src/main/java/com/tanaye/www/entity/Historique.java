package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "historiques")
@Data
@EqualsAndHashCode(callSuper = true)
public class Historique extends EntiteAuditable {

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur; // qui a fait l'action

    @NotBlank
    @Column(nullable = false, length = 150)
    private String action; // libellé concis: CREATION_COLIS, PAIEMENT_REUSSI, etc.

    @Column(length = 1000)
    private String details; // infos additionnelles
}
