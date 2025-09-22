package com.tanaye.www.repository;

import com.tanaye.www.entity.Localisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalisationRepository extends JpaRepository<Localisation, Long> {

    Page<Localisation> findByUtilisateurIdOrderByTimestampDesc(Long utilisateurId, Pageable pageable);
}
