package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import com.tanaye.www.enums.StatutIncident;
import com.tanaye.www.enums.TypeIncident;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "incidents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Incident extends EntiteAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "declencheur_id", nullable = false)
    private Utilisateur declencheur; // qui signale l'incident

    @ManyToOne
    @JoinColumn(name = "colis_id")
    private Colis colis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TypeIncident type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutIncident statut = StatutIncident.OUVERT;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull
    @Column(name = "cgu_applicables", nullable = false)
    private Boolean cguApplicables = true; // rappel du rôle médiateur de Pivoy selon CGU
}
