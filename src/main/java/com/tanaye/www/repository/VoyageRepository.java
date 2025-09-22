package com.tanaye.www.repository;

import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.StatutVoyage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Long> {
    Page<Voyage> findByStatutOrderByDateDepartAsc(StatutVoyage statut, Pageable pageable);

    Page<Voyage> findByVilleDepartIdAndVilleArriveeIdOrderByDateDepartAsc(Long villeDepartId, Long villeArriveeId,
            Pageable pageable);

    Page<Voyage> findByVoyageurIdOrderByDateDepartDesc(Long voyageurId, Pageable pageable);
}

