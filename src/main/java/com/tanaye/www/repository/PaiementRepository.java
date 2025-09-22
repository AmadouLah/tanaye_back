package com.tanaye.www.repository;

import com.tanaye.www.entity.Paiement;
import com.tanaye.www.enums.ModePaiement;
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
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Page<Paiement> findByPayeurIdOrderByDateCreationDesc(Long payeurId, Pageable pageable);

    Page<Paiement> findByColisIdOrderByDateCreationDesc(Long colisId, Pageable pageable);

    Page<Paiement> findByMode(ModePaiement mode, Pageable pageable);

    Optional<Paiement> findFirstByReferenceExterne(String referenceExterne);

    @Query("SELECT COALESCE(SUM(p.montant),0) FROM Paiement p WHERE p.reussi = true")
    BigDecimal totalMontantReussi();

    @Query("SELECT COALESCE(SUM(p.montant),0) FROM Paiement p WHERE p.reussi = true AND p.mode = :mode")
    BigDecimal totalMontantReussiParMode(@Param("mode") ModePaiement mode);

    @Query("SELECT p FROM Paiement p WHERE p.payeur.id = :userId AND p.dateCreation BETWEEN :debut AND :fin ORDER BY p.dateCreation DESC")
    Page<Paiement> findByPayeurEtDateRange(@Param("userId") Long userId, @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.payeur.id = :userId AND p.reussi = :reussi ORDER BY p.dateCreation DESC")
    Page<Paiement> findByPayeurEtStatut(@Param("userId") Long userId, @Param("reussi") boolean reussi, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.payeur.id = :userId AND p.mode = :mode ORDER BY p.dateCreation DESC")
    Page<Paiement> findByPayeurEtMode(@Param("userId") Long userId, @Param("mode") ModePaiement mode, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.payeur.id = :userId AND p.reussi = true")
    long countReussisParPayeur(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.payeur.id = :userId AND p.reussi = false")
    long countEchouesParPayeur(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.montant),0) FROM Paiement p WHERE p.payeur.id = :userId AND p.reussi = true")
    BigDecimal totalMontantReussiParPayeur(@Param("userId") Long userId);

    @Query("SELECT p.mode, COUNT(p), COALESCE(SUM(p.montant),0) FROM Paiement p WHERE p.payeur.id = :userId AND p.reussi = true GROUP BY p.mode")
    List<Object[]> statistiquesParMode(@Param("userId") Long userId);

    @Query("SELECT p FROM Paiement p WHERE p.montant BETWEEN :min AND :max ORDER BY p.dateCreation DESC")
    Page<Paiement> findByMontantRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.reussi = false AND p.dateCreation < :dateLimite")
    List<Paiement> findEchouesAnciens(@Param("dateLimite") LocalDateTime dateLimite);

    @Query("SELECT p FROM Paiement p WHERE p.colis.id = :colisId AND p.reussi = true")
    Optional<Paiement> findPaiementReussiParColis(@Param("colisId") Long colisId);
}
