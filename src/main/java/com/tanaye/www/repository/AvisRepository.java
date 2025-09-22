package com.tanaye.www.repository;

import com.tanaye.www.entity.Avis;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {

    Page<Avis> findByDestinataireIdOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    Page<Avis> findByAuteurIdOrderByDateCreationDesc(Long auteurId, Pageable pageable);

    long countByDestinataireId(Long destinataireId);

    boolean existsByAuteurIdAndDestinataireId(Long auteurId, Long destinataireId);

    @Query("SELECT COALESCE(AVG(a.note),0) FROM Avis a WHERE a.destinataire.id = :userId")
    Double moyenneParUtilisateur(@Param("userId") Long userId);

    interface NoteCountProjection {
        Integer getNote();

        Long getTotal();
    }

    @Query("SELECT a.note AS note, COUNT(a) AS total FROM Avis a WHERE a.destinataire.id = :userId GROUP BY a.note ORDER BY a.note")
    java.util.List<NoteCountProjection> distributionParUtilisateur(@Param("userId") Long userId);

    interface TopVoyageurProjection {
        Long getUtilisateurId();

        String getNom();

        String getPrenom();

        Double getMoyenne();

        Long getTotal();
    }

    @Query("SELECT d.id AS utilisateurId, d.nom AS nom, d.prenom AS prenom, AVG(a.note) AS moyenne, COUNT(a) AS total "
            +
            "FROM Avis a JOIN a.destinataire d " +
            "WHERE d.role = com.tanaye.www.enums.RoleUtilisateur.VOYAGEUR AND d.estVerifie = true " +
            "GROUP BY d.id, d.nom, d.prenom HAVING COUNT(a) >= 5 " +
            "ORDER BY moyenne DESC, total DESC")
    Page<TopVoyageurProjection> topVoyageursFiables(Pageable pageable);
}
