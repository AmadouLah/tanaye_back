package com.tanaye.www.repository;

import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.StatutVoyage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Long> {
    Page<Voyage> findByStatutOrderByDateDepartAsc(StatutVoyage statut, Pageable pageable);

    Page<Voyage> findByVilleDepartIdAndVilleArriveeIdOrderByDateDepartAsc(Long villeDepartId, Long villeArriveeId,
            Pageable pageable);

    Page<Voyage> findByVoyageurIdOrderByDateDepartDesc(Long voyageurId, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.voyageur.id = :voyageurId AND v.dateDepart BETWEEN :debut AND :fin ORDER BY v.dateDepart DESC")
    Page<Voyage> findByVoyageurEtDateRange(@Param("voyageurId") Long voyageurId, @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.voyageur.id = :voyageurId AND v.statut = :statut ORDER BY v.dateDepart DESC")
    Page<Voyage> findByVoyageurEtStatut(@Param("voyageurId") Long voyageurId, @Param("statut") StatutVoyage statut,
            Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.villeDepart.id = :villeDepartId AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByVilleDepartEtStatut(@Param("villeDepartId") Long villeDepartId,
            @Param("statut") StatutVoyage statut, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.villeArrivee.id = :villeArriveeId AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByVilleArriveeEtStatut(@Param("villeArriveeId") Long villeArriveeId,
            @Param("statut") StatutVoyage statut, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.villeDepart.id = :villeDepartId AND v.villeArrivee.id = :villeArriveeId AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByTrajetEtStatut(@Param("villeDepartId") Long villeDepartId,
            @Param("villeArriveeId") Long villeArriveeId, @Param("statut") StatutVoyage statut, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.dateDepart BETWEEN :debut AND :fin AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByDateRangeEtStatut(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin,
            @Param("statut") StatutVoyage statut, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.prix BETWEEN :min AND :max AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByPrixRangeEtStatut(@Param("min") BigDecimal min, @Param("max") BigDecimal max,
            @Param("statut") StatutVoyage statut, Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.poidsMax >= :poids AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByPoidsMinEtStatut(@Param("poids") BigDecimal poids, @Param("statut") StatutVoyage statut,
            Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.voyageur.id = :voyageurId AND v.dateDepart >= :dateLimite ORDER BY v.dateDepart ASC")
    List<Voyage> findFutursParVoyageur(@Param("voyageurId") Long voyageurId,
            @Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT v FROM Voyage v WHERE v.voyageur.id = :voyageurId AND v.dateDepart < :dateLimite ORDER BY v.dateDepart DESC")
    List<Voyage> findPassesParVoyageur(@Param("voyageurId") Long voyageurId,
            @Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT COUNT(v) FROM Voyage v WHERE v.voyageur.id = :voyageurId AND v.statut = :statut")
    long countParVoyageurEtStatut(@Param("voyageurId") Long voyageurId, @Param("statut") StatutVoyage statut);

    @Query("SELECT v.statut, COUNT(v) FROM Voyage v WHERE v.voyageur.id = :voyageurId GROUP BY v.statut")
    List<Object[]> statistiquesParStatut(@Param("voyageurId") Long voyageurId);

    @Query("SELECT v FROM Voyage v WHERE v.statut = 'PLANIFIE' AND v.dateDepart < :dateLimite")
    List<Voyage> findPlanifiesDepasses(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT v FROM Voyage v WHERE v.statut = 'EN_COURS' AND v.dateArriveePrevue < :dateLimite")
    List<Voyage> findEnCoursEnRetard(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT v FROM Voyage v WHERE v.villeDepart.region.pays.id = :paysId AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByPaysEtStatut(@Param("paysId") Long paysId, @Param("statut") StatutVoyage statut,
            Pageable pageable);

    @Query("SELECT v FROM Voyage v WHERE v.villeDepart.region.pays.continent = :continent AND v.statut = :statut ORDER BY v.dateDepart ASC")
    Page<Voyage> findByContinentEtStatut(@Param("continent") String continent, @Param("statut") StatutVoyage statut,
            Pageable pageable);
}
