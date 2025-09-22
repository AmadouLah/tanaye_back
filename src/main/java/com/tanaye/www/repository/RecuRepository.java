package com.tanaye.www.repository;

import com.tanaye.www.entity.Recu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RecuRepository extends JpaRepository<Recu, Long> {
    Optional<Recu> findByPaiementId(Long paiementId);

    Optional<Recu> findByNumero(String numero);

    Page<Recu> findByPayeurIdOrderByDateEmissionDesc(Long payeurId, Pageable pageable);

    Page<Recu> findByDateEmissionBetweenOrderByDateEmissionDesc(LocalDateTime debut, LocalDateTime fin,
            Pageable pageable);
}
