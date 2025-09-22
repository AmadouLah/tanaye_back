package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.ModePaiement;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recus")
@Data
@EqualsAndHashCode(callSuper = true)
public class Recu extends EntiteAuditable {

    @OneToOne
    @JoinColumn(name = "paiement_id", unique = true, nullable = false)
    private Paiement paiement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payeur_id", nullable = false)
    private Utilisateur payeur;

    @ManyToOne
    @JoinColumn(name = "beneficiaire_id")
    private Utilisateur beneficiaire; // voyageur

    @Column(nullable = false, unique = true, length = 50)
    private String numero; // ex: RC-2025-00000123

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModePaiement mode;

    @Column(name = "reference_externe", length = 100)
    private String referenceExterne;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission;

    @Lob
    @Column(name = "contenu", nullable = false)
    private String contenu; // version textuelle du reçu
}
