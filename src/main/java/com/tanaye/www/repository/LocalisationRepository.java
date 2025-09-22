package com.tanaye.www.repository;

import com.tanaye.www.entity.Localisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalisationRepository extends JpaRepository<Localisation, Long> {

    Page<Localisation> findByUtilisateurIdOrderByTimestampDesc(Long utilisateurId, Pageable pageable);

    java.util.Optional<Localisation> findFirstByUtilisateurIdOrderByTimestampDesc(Long utilisateurId);

    Page<Localisation> findByUtilisateurIdAndTimestampBetweenOrderByTimestampDesc(Long utilisateurId,
                                                                                   java.time.LocalDateTime debut,
                                                                                   java.time.LocalDateTime fin,
                                                                                   Pageable pageable);

    @Query("SELECT l FROM Localisation l WHERE l.timestamp BETWEEN :debut AND :fin AND l.latitude BETWEEN :minLat AND :maxLat AND l.longitude BETWEEN :minLng AND :maxLng ORDER BY l.timestamp DESC")
    Page<Localisation> findInBoundingBox(@Param("debut") java.time.LocalDateTime debut,
                                         @Param("fin") java.time.LocalDateTime fin,
                                         @Param("minLat") Double minLat,
                                         @Param("maxLat") Double maxLat,
                                         @Param("minLng") Double minLng,
                                         @Param("maxLng") Double maxLng,
                                         Pageable pageable);
}
