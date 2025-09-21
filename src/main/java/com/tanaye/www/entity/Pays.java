package com.tanaye.www.entity;

import java.util.List;

import com.tanaye.www.entity.base.EntiteAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pays")
@Data
@EqualsAndHashCode(callSuper = true)
public class Pays extends EntiteAuditable {
    @Column(nullable = false, unique = true)
    private String nom;

    @Column(name = "code_iso", nullable = false, unique = true, length = 3)
    private String codeIso; // exemple: "ML" pour Mali

    @OneToMany(mappedBy = "pays")
    private List<Region> regions;
}

