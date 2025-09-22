package com.tanaye.www.repository;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByTelephone(String telephone);

    boolean existsByEmail(String email);

    long countByRole(RoleUtilisateur role);

    Page<Utilisateur> findByRoleOrderByDateCreationDesc(RoleUtilisateur role, Pageable pageable);

    Page<Utilisateur> findByStatutOrderByDateCreationDesc(StatutUtilisateur statut, Pageable pageable);

    Page<Utilisateur> findByEstVerifieTrueOrderByDateCreationDesc(Pageable pageable);

    Page<Utilisateur> findByRoleAndStatutOrderByDateCreationDesc(RoleUtilisateur role, StatutUtilisateur statut,
            Pageable pageable);

    Page<Utilisateur> findByRoleAndEstVerifieTrueOrderByDateCreationDesc(RoleUtilisateur role, Pageable pageable);

    Page<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom,
            String prenom, String email, Pageable pageable);

    @Query("SELECT u FROM Utilisateur u WHERE (LOWER(u.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY u.dateCreation DESC")
    Page<Utilisateur> rechercher(@Param("q") String query, Pageable pageable);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND (LOWER(u.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY u.dateCreation DESC")
    Page<Utilisateur> rechercherParRole(@Param("role") RoleUtilisateur role, @Param("q") String query, Pageable pageable);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND u.estVerifie = true ORDER BY u.dateCreation DESC")
    Page<Utilisateur> findVerifiesParRole(@Param("role") RoleUtilisateur role, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.estVerifie = true")
    long countVerifies();

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role AND u.estVerifie = true")
    long countVerifiesParRole(@Param("role") RoleUtilisateur role);

    @Query("SELECT u FROM Utilisateur u WHERE u.statut = 'ACTIF' AND u.derniereConnexion IS NOT NULL AND u.derniereConnexion > :limite ORDER BY u.derniereConnexion DESC")
    List<Utilisateur> actifsRecents(@Param("limite") java.time.LocalDateTime limite);
}
