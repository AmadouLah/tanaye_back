package com.tanaye.www.repository;

import com.tanaye.www.entity.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {
    Page<Ville> findByRegionIdOrderByNomAsc(Long regionId, Pageable pageable);
}
