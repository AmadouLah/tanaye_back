package com.tanaye.www.repository;

import com.tanaye.www.entity.Reclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    Page<Reclamation> findByExpediteurIdOrderByDateCreationDesc(Long expediteurId, Pageable pageable);

    Page<Reclamation> findByColisIdOrderByDateCreationDesc(Long colisId, Pageable pageable);
}
