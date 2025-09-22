package com.tanaye.www.repository;

import com.tanaye.www.entity.Historique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {

    Page<Historique> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId, Pageable pageable);

    Page<Historique> findByActionContainingIgnoreCaseOrderByDateCreationDesc(String action, Pageable pageable);

    Page<Historique> findByDateCreationBetweenOrderByDateCreationDesc(java.time.LocalDateTime debut,
            java.time.LocalDateTime fin,
            Pageable pageable);

    @Query("SELECT h FROM Historique h WHERE (:userId IS NULL OR h.utilisateur.id = :userId) " +
            "AND (:action IS NULL OR LOWER(h.action) LIKE LOWER(CONCAT('%', :action, '%'))) " +
            "AND (:debut IS NULL OR h.dateCreation >= :debut) " +
            "AND (:fin IS NULL OR h.dateCreation <= :fin) ORDER BY h.dateCreation DESC")
    Page<Historique> rechercher(@Param("userId") Long userId,
            @Param("action") String action,
            @Param("debut") java.time.LocalDateTime debut,
            @Param("fin") java.time.LocalDateTime fin,
            Pageable pageable);
}
