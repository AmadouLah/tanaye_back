package com.tanaye.www.entity;

import java.util.List;

import com.tanaye.www.entity.base.EntiteAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "regions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Region extends EntiteAuditable {
    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_id", nullable = false)
    private Pays pays;

    @OneToMany(mappedBy = "region")
    private List<Ville> villes;
}
