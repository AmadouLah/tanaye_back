package com.tanaye.www.repository;

import com.tanaye.www.entity.Incident;
import com.tanaye.www.enums.StatutIncident;
import com.tanaye.www.enums.TypeIncident;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByDeclencheurIdOrderByDateCreationDesc(Long declencheurId, Pageable pageable);

    Page<Incident> findByColisIdOrderByDateCreationDesc(Long colisId, Pageable pageable);

    Page<Incident> findByTypeOrderByDateCreationDesc(TypeIncident type, Pageable pageable);

    Page<Incident> findByStatutOrderByDateCreationDesc(StatutIncident statut, Pageable pageable);

    Page<Incident> findByDateCreationBetweenOrderByDateCreationDesc(java.time.LocalDateTime debut,
            java.time.LocalDateTime fin,
            Pageable pageable);

    @Query("SELECT i FROM Incident i WHERE (:type IS NULL OR i.type = :type) " +
            "AND (:statut IS NULL OR i.statut = :statut) " +
            "AND (:userId IS NULL OR i.declencheur.id = :userId) " +
            "AND (:colisId IS NULL OR i.colis.id = :colisId) " +
            "AND (:debut IS NULL OR i.dateCreation >= :debut) " +
            "AND (:fin IS NULL OR i.dateCreation <= :fin) ORDER BY i.dateCreation DESC")
    Page<Incident> rechercher(@Param("type") TypeIncident type,
            @Param("statut") StatutIncident statut,
            @Param("userId") Long userId,
            @Param("colisId") Long colisId,
            @Param("debut") java.time.LocalDateTime debut,
            @Param("fin") java.time.LocalDateTime fin,
            Pageable pageable);
}
