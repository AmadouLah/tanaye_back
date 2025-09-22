package com.tanaye.www.repository;

import com.tanaye.www.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByDestinataireIdOrderByDateCreationDesc(Long utilisateurId, Pageable pageable);

    long countByDestinataireIdAndEstLuFalse(Long utilisateurId);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.id = :userId AND n.estLu = false ORDER BY n.dateCreation DESC")
    Page<Notification> findNonLues(@Param("userId") Long userId, Pageable pageable);
}
