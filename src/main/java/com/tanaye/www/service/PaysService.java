package com.tanaye.www.service;

import com.tanaye.www.entity.Pays;
import com.tanaye.www.repository.PaysRepository;
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
public class PaysService {

    private final PaysRepository paysRepository;

    public Pays creer(Pays p) {
        log.info("Création pays {}", p.getNom());
        return paysRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Page<Pays> rechercher(String nom, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (nom == null || nom.isBlank())
            return paysRepository.findAll(pageable);
        return paysRepository.findByNomContainingIgnoreCaseOrderByNomAsc(nom, pageable);
    }
}
