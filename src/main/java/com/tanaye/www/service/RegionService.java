package com.tanaye.www.service;

import com.tanaye.www.entity.Pays;
import com.tanaye.www.entity.Region;
import com.tanaye.www.repository.PaysRepository;
import com.tanaye.www.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RegionService {

    private final RegionRepository regionRepository;
    private final PaysRepository paysRepository;

    public Region creer(Long paysId, Region r) {
        Pays p = paysRepository.findById(paysId)
                .orElseThrow(() -> new IllegalArgumentException("Pays introuvable: " + paysId));
        r.setPays(p);
        return regionRepository.save(r);
    }

    @Transactional(readOnly = true)
    public Page<Region> listerParPays(Long paysId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return regionRepository.findByPaysIdOrderByNomAsc(paysId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Region> rechercher(Long paysId, String query, Pageable pageable) {
        if (paysId != null && query != null && !query.trim().isEmpty()) {
            return regionRepository.rechercherParPays(paysId, query, pageable);
        }
        if (query != null && !query.trim().isEmpty()) {
            return regionRepository.rechercher(query, pageable);
        }
        if (paysId != null) {
            return regionRepository.findByPaysIdOrderByNomAsc(paysId, pageable);
        }
        return regionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Region> listerActifs() {
        return regionRepository.findAllActifs();
    }

    @Transactional(readOnly = true)
    public List<Region> listerActifsParPays(Long paysId) {
        return regionRepository.findActifsParPays(paysId);
    }

    @Transactional(readOnly = true)
    public long countActifs() {
        return regionRepository.countActifs();
    }

    @Transactional(readOnly = true)
    public long countActifsParPays(Long paysId) {
        return regionRepository.countActifsParPays(paysId);
    }

    public Region activer(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("Région introuvable: " + regionId));
        region.setActif(true);
        return regionRepository.save(region);
    }

    public Region desactiver(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("Région introuvable: " + regionId));
        region.setActif(false);
        return regionRepository.save(region);
    }
}
