package com.tanaye.www.repository;

import com.tanaye.www.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByAuteurIdOrderByDateEnvoiDesc(Long auteurId, Pageable pageable);

    Page<Message> findByDestinataireIdOrderByDateEnvoiDesc(Long destinataireId, Pageable pageable);

    long countByDestinataireIdAndEstLuFalse(Long destinataireId);

    @Query("SELECT m FROM Message m WHERE (m.auteur.id = :u1 AND m.destinataire.id = :u2) OR (m.auteur.id = :u2 AND m.destinataire.id = :u1) ORDER BY m.dateEnvoi DESC")
    Page<Message> conversation(@Param("u1") Long utilisateur1, @Param("u2") Long utilisateur2, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.estLu = true WHERE m.auteur.id = :auteurId AND m.destinataire.id = :destinataireId AND m.estLu = false")
    int marquerConversationLue(@Param("destinataireId") Long destinataireId, @Param("auteurId") Long auteurId);

    interface UnreadCountProjection {
        Long getInterlocuteurId();

        Long getTotal();
    }

    @Query("SELECT m.auteur.id AS interlocuteurId, COUNT(m) AS total FROM Message m WHERE m.destinataire.id = :destinataireId AND m.estLu = false GROUP BY m.auteur.id")
    java.util.List<UnreadCountProjection> nonLusParInterlocuteur(@Param("destinataireId") Long destinataireId);

    interface ThreadHeadProjection {
        Long getInterlocuteurId();

        java.time.LocalDateTime getLastDate();
    }

    @Query("SELECT CASE WHEN m.auteur.id = :userId THEN m.destinataire.id ELSE m.auteur.id END AS interlocuteurId, MAX(m.dateEnvoi) AS lastDate "
            +
            "FROM Message m WHERE (m.auteur.id = :userId OR m.destinataire.id = :userId) " +
            "AND (:q IS NULL OR :q = '' OR " +
            "     (CASE WHEN m.auteur.id = :userId THEN LOWER(m.destinataire.nom) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "           ELSE LOWER(m.auteur.nom) LIKE LOWER(CONCAT('%', :q, '%')) END) OR " +
            "     (CASE WHEN m.auteur.id = :userId THEN LOWER(m.destinataire.prenom) LIKE LOWER(CONCAT('%', :q, '%')) "
            +
            "           ELSE LOWER(m.auteur.prenom) LIKE LOWER(CONCAT('%', :q, '%')) END)) " +
            "GROUP BY CASE WHEN m.auteur.id = :userId THEN m.destinataire.id ELSE m.auteur.id END " +
            "ORDER BY lastDate DESC")
    java.util.List<ThreadHeadProjection> threads(@Param("userId") Long userId, @Param("q") String query);
}
