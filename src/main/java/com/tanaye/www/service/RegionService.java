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
}
