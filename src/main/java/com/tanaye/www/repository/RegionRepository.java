package com.tanaye.www.repository;

import com.tanaye.www.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Page<Region> findByPaysIdOrderByNomAsc(Long paysId, Pageable pageable);
}
