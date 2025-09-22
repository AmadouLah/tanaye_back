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
}
