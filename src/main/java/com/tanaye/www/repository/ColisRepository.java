package com.tanaye.www.repository;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.enums.StatutColis;
import com.tanaye.www.enums.TypeColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisRepository extends JpaRepository<Colis, Long> {
    Page<Colis> findByExpediteurIdOrderByDateCreationDesc(Long expediteurId, Pageable pageable);

    Page<Colis> findByDestinataireIdOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    Page<Colis> findByVoyageIdOrderByDateCreationDesc(Long voyageId, Pageable pageable);

    Page<Colis> findByStatutOrderByDateCreationDesc(StatutColis statut, Pageable pageable);

    Page<Colis> findByTypeOrderByDateCreationDesc(TypeColis type, Pageable pageable);

    Page<Colis> findByVilleDepartIdAndVilleArriveeIdOrderByDateCreationDesc(Long villeDepartId, Long villeArriveeId,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.poids),0) FROM Colis c WHERE c.voyage.id = :voyageId")
    Double totalPoidsParVoyage(@Param("voyageId") Long voyageId);
}
