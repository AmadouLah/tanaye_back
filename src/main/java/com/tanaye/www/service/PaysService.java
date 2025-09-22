package com.tanaye.www.service;

import com.tanaye.www.dto.PaysRechercheDTO;
import com.tanaye.www.entity.Pays;
import com.tanaye.www.enums.Continent;
import com.tanaye.www.repository.PaysRepository;
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
public class PaysService {

    private final PaysRepository paysRepository;

    public Pays creer(Pays p) {
        log.info("Création pays {}", p.getNom());
        return paysRepository.save(p);
    }

    public Pays creer(String nom, String codeIso, Continent continent) {
        log.info("Création pays: {} ({}) - {}", nom, codeIso, continent);
        Pays pays = new Pays();
        pays.setNom(nom);
        pays.setCodeIso(codeIso);
        pays.setContinent(continent != null ? continent.name() : null);
        pays.setActif(true);
        return paysRepository.save(pays);
    }

    @Transactional(readOnly = true)
    public Page<Pays> rechercher(String nom, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (nom == null || nom.isBlank())
            return paysRepository.findAll(pageable);
        return paysRepository.findByNomContainingIgnoreCaseOrderByNomAsc(nom, pageable);
    }

    @Transactional(readOnly = true)
    public List<Pays> listerActifs() {
        return paysRepository.findAllActifs();
    }

    @Transactional(readOnly = true)
    public Optional<Pays> parCodeIso(String codeIso) {
        return paysRepository.findByCodeIso(codeIso);
    }

    @Transactional(readOnly = true)
    public Optional<Pays> parCodeIsoActif(String codeIso) {
        return paysRepository.findActifByCodeIso(codeIso);
    }

    @Transactional(readOnly = true)
    public Page<Pays> rechercher(PaysRechercheDTO criteres, Pageable pageable) {
        if (criteres.getQuery() != null && !criteres.getQuery().trim().isEmpty()) {
            return paysRepository.rechercher(criteres.getQuery(), pageable);
        }
        if (criteres.getContinent() != null) {
            List<Pays> resultats = paysRepository.findByContinent(criteres.getContinent().name());
            return new org.springframework.data.domain.PageImpl<>(resultats, pageable, resultats.size());
        }
        if (Boolean.TRUE.equals(criteres.getActif())) {
            List<Pays> resultats = paysRepository.findAllActifs();
            return new org.springframework.data.domain.PageImpl<>(resultats, pageable, resultats.size());
        }
        return paysRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Pays> rechercherActifs(String query) {
        return paysRepository.rechercherActifs(query);
    }

    @Transactional(readOnly = true)
    public List<Pays> parCodesIso(List<String> codes) {
        return paysRepository.findByCodesIso(codes);
    }

    @Transactional(readOnly = true)
    public List<Pays> parContinent(Continent continent) {
        return paysRepository.findByContinent(continent.name());
    }

    @Transactional(readOnly = true)
    public List<String> tousContinents() {
        return paysRepository.findAllContinents();
    }

    @Transactional(readOnly = true)
    public long countActifs() {
        return paysRepository.countActifs();
    }

    public Pays activer(Long paysId) {
        Pays pays = paysRepository.findById(paysId)
                .orElseThrow(() -> new IllegalArgumentException("Pays introuvable: " + paysId));
        pays.setActif(true);
        return paysRepository.save(pays);
    }

    public Pays desactiver(Long paysId) {
        Pays pays = paysRepository.findById(paysId)
                .orElseThrow(() -> new IllegalArgumentException("Pays introuvable: " + paysId));
        pays.setActif(false);
        return paysRepository.save(pays);
    }
}
