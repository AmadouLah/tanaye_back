package com.tanaye.www.repository;

import com.tanaye.www.entity.Pays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {
    Optional<Pays> findByCodeIso(String codeIso);

    Page<Pays> findByNomContainingIgnoreCaseOrderByNomAsc(String nom, Pageable pageable);
}
