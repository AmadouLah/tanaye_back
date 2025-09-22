package com.tanaye.www.service;

import com.tanaye.www.entity.Region;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.repository.RegionRepository;
import com.tanaye.www.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VilleService {

    private final VilleRepository villeRepository;
    private final RegionRepository regionRepository;

    public Ville creer(Long regionId, Ville v) {
        Region r = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("Région introuvable: " + regionId));
        v.setRegion(r);
        return villeRepository.save(v);
    }

    @Transactional(readOnly = true)
    public Page<Ville> listerParRegion(Long regionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return villeRepository.findByRegionIdOrderByNomAsc(regionId, pageable);
    }
}
