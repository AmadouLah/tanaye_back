package com.tanaye.www.service;

import com.tanaye.www.dto.VilleRechercheDTO;
import com.tanaye.www.entity.Region;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.enums.TypeVille;
import com.tanaye.www.repository.RegionRepository;
import com.tanaye.www.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VilleService {

    private final VilleRepository villeRepository;
    private final RegionRepository regionRepository;

    public Ville creer(Long regionId, Ville ville) {
        return creer(regionId, ville, TypeVille.AUTRE);
    }

    public Ville creer(Long regionId, Ville ville, TypeVille type) {
        log.info("Création ville: {} dans région {}", ville.getNom(), regionId);
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("Région introuvable: " + regionId));
        ville.setRegion(region);
        ville.setType(type.name());
        ville.setActif(true);
        return villeRepository.save(ville);
    }

    @Transactional(readOnly = true)
    public Page<Ville> listerParRegion(Long regionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return villeRepository.findByRegionIdOrderByNomAsc(regionId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Ville> listerActifs() {
        return villeRepository.findAllActifs();
    }

    @Transactional(readOnly = true)
    public List<Ville> listerActifsParRegion(Long regionId) {
        return villeRepository.findActifsParRegion(regionId);
    }

    @Transactional(readOnly = true)
    public Page<Ville> rechercher(String query, Pageable pageable) {
        return villeRepository.rechercher(query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Ville> rechercherParRegion(Long regionId, String query, Pageable pageable) {
        return villeRepository.rechercherParRegion(regionId, query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Ville> rechercher(VilleRechercheDTO criteres, Pageable pageable) {
        return villeRepository.rechercher(criteres.getQuery(), pageable);
    }

    @Transactional(readOnly = true)
    public List<Ville> rechercherActifs(String query) {
        return villeRepository.rechercherActifs(query);
    }

    @Transactional(readOnly = true)
    public List<Ville> listerActifsParPays(Long paysId) {
        return villeRepository.findActifsParPays(paysId);
    }

    @Transactional(readOnly = true)
    public List<Ville> rechercherActifsParPays(Long paysId, String query) {
        return villeRepository.rechercherActifsParPays(paysId, query);
    }

    @Transactional(readOnly = true)
    public List<Ville> listerActifsParCodePays(String codePays) {
        return villeRepository.findActifsParCodePays(codePays);
    }

    @Transactional(readOnly = true)
    public List<Ville> listerActifsParContinent(String continent) {
        return villeRepository.findActifsParContinent(continent);
    }

    @Transactional(readOnly = true)
    public long countActifs() {
        return villeRepository.countActifs();
    }

    @Transactional(readOnly = true)
    public long countActifsParRegion(Long regionId) {
        return villeRepository.countActifsParRegion(regionId);
    }

    @Transactional(readOnly = true)
    public Optional<Ville> parNomEtRegion(String nom, Long regionId) {
        return villeRepository.findByNomEtRegion(nom, regionId);
    }

    @Transactional(readOnly = true)
    public Optional<Ville> parNomEtRegionActif(String nom, Long regionId) {
        return villeRepository.findActifByNomEtRegion(nom, regionId);
    }

    public Ville activer(Long villeId) {
        Ville ville = villeRepository.findById(villeId)
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable: " + villeId));
        ville.setActif(true);
        return villeRepository.save(ville);
    }

    public Ville desactiver(Long villeId) {
        Ville ville = villeRepository.findById(villeId)
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable: " + villeId));
        ville.setActif(false);
        return villeRepository.save(ville);
    }
}
