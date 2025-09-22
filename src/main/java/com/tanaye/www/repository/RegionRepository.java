package com.tanaye.www.repository;

import com.tanaye.www.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Page<Region> findByPaysIdOrderByNomAsc(Long paysId, Pageable pageable);

    @Query("SELECT r FROM Region r WHERE r.pays.id = :paysId AND LOWER(r.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY r.nom ASC")
    Page<Region> rechercherParPays(@Param("paysId") Long paysId, @Param("q") String query, Pageable pageable);

    @Query("SELECT r FROM Region r WHERE LOWER(r.nom) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY r.nom ASC")
    Page<Region> rechercher(@Param("q") String query, Pageable pageable);

    @Query("SELECT r FROM Region r WHERE r.actif = true ORDER BY r.nom ASC")
    List<Region> findAllActifs();

    @Query("SELECT r FROM Region r WHERE r.pays.id = :paysId AND r.actif = true ORDER BY r.nom ASC")
    List<Region> findActifsParPays(@Param("paysId") Long paysId);

    @Query("SELECT COUNT(r) FROM Region r WHERE r.actif = true")
    long countActifs();

    @Query("SELECT COUNT(r) FROM Region r WHERE r.pays.id = :paysId AND r.actif = true")
    long countActifsParPays(@Param("paysId") Long paysId);
}
