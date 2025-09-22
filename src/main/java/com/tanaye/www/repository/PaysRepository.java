package com.tanaye.www.repository;

import com.tanaye.www.entity.Pays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {
    Optional<Pays> findByCodeIso(String codeIso);

    Page<Pays> findByNomContainingIgnoreCaseOrderByNomAsc(String nom, Pageable pageable);

    @Query("SELECT p FROM Pays p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.codeIso) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY p.nom ASC")
    Page<Pays> rechercher(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Pays p WHERE p.actif = true ORDER BY p.nom ASC")
    List<Pays> findAllActifs();

    @Query("SELECT p FROM Pays p WHERE p.actif = true AND LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY p.nom ASC")
    List<Pays> rechercherActifs(@Param("q") String query);

    @Query("SELECT COUNT(p) FROM Pays p WHERE p.actif = true")
    long countActifs();

    @Query("SELECT p FROM Pays p WHERE p.codeIso IN :codes ORDER BY p.nom ASC")
    List<Pays> findByCodesIso(@Param("codes") List<String> codes);

    @Query("SELECT p FROM Pays p WHERE p.continent = :continent AND p.actif = true ORDER BY p.nom ASC")
    List<Pays> findByContinent(@Param("continent") String continent);

    @Query("SELECT DISTINCT p.continent FROM Pays p WHERE p.continent IS NOT NULL ORDER BY p.continent ASC")
    List<String> findAllContinents();

    @Query("SELECT p FROM Pays p WHERE p.actif = true AND p.codeIso = :codeIso")
    Optional<Pays> findActifByCodeIso(@Param("codeIso") String codeIso);
}
