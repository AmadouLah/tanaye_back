package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.ModePaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "paiements")
@Data
@EqualsAndHashCode(callSuper = true)
public class Paiement extends EntiteAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "payeur_id", nullable = false)
    private Utilisateur payeur;

    @ManyToOne
    @JoinColumn(name = "colis_id")
    private Colis colis;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 30)
    private ModePaiement mode;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "reference_externe", length = 100)
    private String referenceExterne; // ID du PSP (CB/OM/SAMA)

    @Column(name = "reussi", nullable = false)
    private Boolean reussi = false;
}
