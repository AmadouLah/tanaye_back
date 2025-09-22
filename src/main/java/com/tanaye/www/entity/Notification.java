package com.tanaye.www.entity;

import com.tanaye.www.entity.base.EntiteAuditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends EntiteAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur destinataire;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String titre;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String contenu;

    @NotNull
    @Column(name = "est_lu", nullable = false)
    private Boolean estLu = false;
}
