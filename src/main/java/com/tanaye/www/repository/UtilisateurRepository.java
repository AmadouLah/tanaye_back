package com.tanaye.www.repository;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
