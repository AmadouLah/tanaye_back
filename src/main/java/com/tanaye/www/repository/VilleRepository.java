package com.tanaye.www.repository;

import com.tanaye.www.entity.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {
    Page<Ville> findByRegionIdOrderByNomAsc(Long regionId, Pageable pageable);

    @Query("SELECT v FROM Ville v WHERE LOWER(v.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY v.nom ASC")
    Page<Ville> rechercher(@Param("q") String query, Pageable pageable);

    @Query("SELECT v FROM Ville v WHERE v.region.id = :regionId AND LOWER(v.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY v.nom ASC")
    Page<Ville> rechercherParRegion(@Param("regionId") Long regionId, @Param("q") String query, Pageable pageable);

    @Query("SELECT v FROM Ville v WHERE v.actif = true ORDER BY v.nom ASC")
    List<Ville> findAllActifs();

    @Query("SELECT v FROM Ville v WHERE v.region.id = :regionId AND v.actif = true ORDER BY v.nom ASC")
    List<Ville> findActifsParRegion(@Param("regionId") Long regionId);

    @Query("SELECT v FROM Ville v WHERE v.actif = true AND LOWER(v.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY v.nom ASC")
    List<Ville> rechercherActifs(@Param("q") String query);

    @Query("SELECT COUNT(v) FROM Ville v WHERE v.actif = true")
    long countActifs();

    @Query("SELECT COUNT(v) FROM Ville v WHERE v.region.id = :regionId AND v.actif = true")
    long countActifsParRegion(@Param("regionId") Long regionId);

    @Query("SELECT v FROM Ville v WHERE v.nom = :nom AND v.region.id = :regionId")
    Optional<Ville> findByNomEtRegion(@Param("nom") String nom, @Param("regionId") Long regionId);

    @Query("SELECT v FROM Ville v WHERE v.actif = true AND v.nom = :nom AND v.region.id = :regionId")
    Optional<Ville> findActifByNomEtRegion(@Param("nom") String nom, @Param("regionId") Long regionId);

    @Query("SELECT v FROM Ville v WHERE v.region.pays.id = :paysId AND v.actif = true ORDER BY v.nom ASC")
    List<Ville> findActifsParPays(@Param("paysId") Long paysId);

    @Query("SELECT v FROM Ville v WHERE v.region.pays.id = :paysId AND v.actif = true AND LOWER(v.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY v.nom ASC")
    List<Ville> rechercherActifsParPays(@Param("paysId") Long paysId, @Param("q") String query);

    @Query("SELECT v FROM Ville v WHERE v.region.pays.codeIso = :codeIso AND v.actif = true ORDER BY v.nom ASC")
    List<Ville> findActifsParCodePays(@Param("codeIso") String codeIso);

    @Query("SELECT v FROM Ville v WHERE v.region.pays.continent = :continent AND v.actif = true ORDER BY v.nom ASC")
    List<Ville> findActifsParContinent(@Param("continent") String continent);
}
