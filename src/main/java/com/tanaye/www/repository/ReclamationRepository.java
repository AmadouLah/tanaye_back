package com.tanaye.www.repository;

import com.tanaye.www.entity.Reclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    Page<Reclamation> findByExpediteurIdOrderByDateCreationDesc(Long expediteurId, Pageable pageable);

    Page<Reclamation> findByColisIdOrderByDateCreationDesc(Long colisId, Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.expediteur.id = :userId AND r.dateCreation BETWEEN :debut AND :fin ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByExpediteurEtDateRange(@Param("userId") Long userId, @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin, Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.expediteur.id = :userId AND r.statut = :statut ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByExpediteurEtStatut(@Param("userId") Long userId, @Param("statut") String statut,
            Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.expediteur.id = :userId AND r.type = :type ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByExpediteurEtType(@Param("userId") Long userId, @Param("type") String type,
            Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.expediteur.id = :userId AND (LOWER(r.objet) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY r.dateCreation DESC")
    Page<Reclamation> rechercherParExpediteur(@Param("userId") Long userId, @Param("q") String query,
            Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reclamation r WHERE r.expediteur.id = :userId AND r.statut = :statut")
    long countParExpediteurEtStatut(@Param("userId") Long userId, @Param("statut") String statut);

    @Query("SELECT COUNT(r) FROM Reclamation r WHERE r.expediteur.id = :userId AND r.type = :type")
    long countParExpediteurEtType(@Param("userId") Long userId, @Param("type") String type);

    @Query("SELECT r.statut, COUNT(r) FROM Reclamation r WHERE r.expediteur.id = :userId GROUP BY r.statut")
    List<Object[]> statistiquesParStatut(@Param("userId") Long userId);

    @Query("SELECT r.type, COUNT(r) FROM Reclamation r WHERE r.expediteur.id = :userId GROUP BY r.type")
    List<Object[]> statistiquesParType(@Param("userId") Long userId);

    @Query("SELECT r FROM Reclamation r WHERE r.statut = :statut ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByStatut(@Param("statut") String statut, Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.type = :type ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByType(@Param("type") String type, Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.dateCreation BETWEEN :debut AND :fin ORDER BY r.dateCreation DESC")
    Page<Reclamation> findByDateRange(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin,
            Pageable pageable);

    @Query("SELECT r FROM Reclamation r WHERE r.statut = 'EN_ATTENTE' AND r.dateCreation < :dateLimite")
    List<Reclamation> findEnAttenteAnciennes(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT r FROM Reclamation r WHERE r.colis.id = :colisId AND r.statut IN ('EN_COURS', 'RESOLUE')")
    List<Reclamation> findReclamationsActivesParColis(@Param("colisId") Long colisId);
}
