package com.tanaye.www.service;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public Utilisateur creer(Utilisateur u) {
        log.info("Création utilisateur {} {}", u.getPrenom(), u.getNom());
        return utilisateurRepository.save(u);
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> parEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<Utilisateur> lister(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return utilisateurRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Utilisateur> rechercher(String terme, RoleUtilisateur role, StatutUtilisateur statut, Boolean verifie,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (terme != null && !terme.isBlank() && role != null) {
            return utilisateurRepository.rechercherParRole(role, terme, pageable);
        }
        if (role != null && statut != null) {
            return utilisateurRepository.findByRoleAndStatutOrderByDateCreationDesc(role, statut, pageable);
        }
        if (role != null && Boolean.TRUE.equals(verifie)) {
            return utilisateurRepository.findByRoleAndEstVerifieTrueOrderByDateCreationDesc(role, pageable);
        }
        if (role != null) {
            return utilisateurRepository.findByRoleOrderByDateCreationDesc(role, pageable);
        }
        if (statut != null) {
            return utilisateurRepository.findByStatutOrderByDateCreationDesc(statut, pageable);
        }
        if (Boolean.TRUE.equals(verifie)) {
            return utilisateurRepository.findByEstVerifieTrueOrderByDateCreationDesc(pageable);
        }
        if (terme != null && !terme.isBlank()) {
            return utilisateurRepository.rechercher(terme, pageable);
        }
        return utilisateurRepository.findAll(pageable);
    }

    public Utilisateur mettreAJourStatut(Long utilisateurId, StatutUtilisateur statut, Boolean estVerifie) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + utilisateurId));
        if (statut != null)
            u.setStatut(statut);
        if (estVerifie != null)
            u.setEstVerifie(estVerifie);
        return utilisateurRepository.save(u);
    }

    @Transactional(readOnly = true)
    public long compterParRole(RoleUtilisateur role) {
        return utilisateurRepository.countByRole(role);
    }

    @Transactional(readOnly = true)
    public Page<Utilisateur> listerVerifiesParRole(RoleUtilisateur role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return utilisateurRepository.findByRoleAndEstVerifieTrueOrderByDateCreationDesc(role, pageable);
    }

    @Transactional(readOnly = true)
    public long compterVerifies() {
        return utilisateurRepository.countVerifies();
    }

    @Transactional(readOnly = true)
    public long compterVerifiesParRole(RoleUtilisateur role) {
        return utilisateurRepository.countVerifiesParRole(role);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> actifsRecents(int jours) {
        LocalDateTime limite = LocalDateTime.now().minusDays(jours);
        return utilisateurRepository.actifsRecents(limite);
    }
}
