package com.tanaye.www.repository;

import com.tanaye.www.entity.Recu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecuRepository extends JpaRepository<Recu, Long> {
        Optional<Recu> findByPaiementId(Long paiementId);

        Optional<Recu> findByNumero(String numero);

        Page<Recu> findByPayeurIdOrderByDateEmissionDesc(Long payeurId, Pageable pageable);

        Page<Recu> findByDateEmissionBetweenOrderByDateEmissionDesc(LocalDateTime debut, LocalDateTime fin,
                        Pageable pageable);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND r.dateEmission BETWEEN :debut AND :fin ORDER BY r.dateEmission DESC")
        Page<Recu> findByPayeurEtDateRange(@Param("userId") Long userId, @Param("debut") LocalDateTime debut,
                        @Param("fin") LocalDateTime fin, Pageable pageable);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND r.montant BETWEEN :min AND :max ORDER BY r.dateEmission DESC")
        Page<Recu> findByPayeurEtMontantRange(@Param("userId") Long userId, @Param("min") BigDecimal min,
                        @Param("max") BigDecimal max, Pageable pageable);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND LOWER(r.numero) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY r.dateEmission DESC")
        Page<Recu> rechercherParPayeur(@Param("userId") Long userId, @Param("q") String query, Pageable pageable);

        @Query("SELECT COUNT(r) FROM Recu r WHERE r.payeur.id = :userId")
        long countParPayeur(@Param("userId") Long userId);

        @Query("SELECT COALESCE(SUM(r.montant),0) FROM Recu r WHERE r.payeur.id = :userId")
        BigDecimal totalMontantParPayeur(@Param("userId") Long userId);

        @Query("SELECT COALESCE(AVG(r.montant),0) FROM Recu r WHERE r.payeur.id = :userId")
        BigDecimal montantMoyenParPayeur(@Param("userId") Long userId);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND r.dateEmission >= :dateLimite ORDER BY r.dateEmission DESC")
        List<Recu> findRecentsParPayeur(@Param("userId") Long userId, @Param("dateLimite") LocalDateTime dateLimite);

        @Query("SELECT r FROM Recu r WHERE r.dateEmission BETWEEN :debut AND :fin ORDER BY r.dateEmission DESC")
        List<Recu> findByDateRange(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

        @Query("SELECT r FROM Recu r WHERE r.montant >= :montantMin ORDER BY r.dateEmission DESC")
        Page<Recu> findByMontantMin(@Param("montantMin") BigDecimal montantMin, Pageable pageable);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND YEAR(r.dateEmission) = :annee ORDER BY r.dateEmission DESC")
        List<Recu> findByPayeurEtAnnee(@Param("userId") Long userId, @Param("annee") int annee);

        @Query("SELECT YEAR(r.dateEmission) as annee, COUNT(r) as count, COALESCE(SUM(r.montant),0) as total FROM Recu r WHERE r.payeur.id = :userId GROUP BY YEAR(r.dateEmission) ORDER BY annee DESC")
        List<Object[]> statistiquesParAnnee(@Param("userId") Long userId);

        @Query("SELECT r FROM Recu r WHERE r.dateEmission < :dateLimite")
        List<Recu> findAnciens(@Param("dateLimite") LocalDateTime dateLimite);

        @Query("SELECT r FROM Recu r WHERE r.payeur.id = :userId AND r.paiement.id = :paiementId")
        Optional<Recu> findByPayeurEtPaiement(@Param("userId") Long userId, @Param("paiementId") Long paiementId);
}
