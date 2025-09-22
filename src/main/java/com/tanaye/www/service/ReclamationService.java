package com.tanaye.www.service;

import com.tanaye.www.dto.ReclamationRechercheDTO;
import com.tanaye.www.dto.ReclamationStatistiquesDTO;
import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Reclamation;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.StatutReclamation;
import com.tanaye.www.enums.TypeReclamation;
import com.tanaye.www.repository.ColisRepository;
import com.tanaye.www.repository.ReclamationRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ColisRepository colisRepository;
    private final HistoriqueService historiqueService;

    public Reclamation creer(Long expediteurId, Long colisId, String objet, String description, boolean accepteCgu) {
        return creer(expediteurId, colisId, objet, description, accepteCgu, TypeReclamation.AUTRE);
    }

    public Reclamation creer(Long expediteurId, Long colisId, String objet, String description, boolean accepteCgu,
            TypeReclamation type) {
        log.info("Création réclamation: expediteur={}, colis={}, type={}", expediteurId, colisId, type);
        if (!accepteCgu)
            throw new IllegalStateException("CGU non acceptées");
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + expediteurId));
        Colis colis = (colisId != null) ? colisRepository.findById(colisId).orElse(null) : null;

        Reclamation r = new Reclamation();
        r.setExpediteur(expediteur);
        r.setColis(colis);
        r.setObjet(objet);
        r.setDescription(description);
        r.setType(type.name());
        r.setStatut(StatutReclamation.EN_ATTENTE.name());
        r.setAccepteCgu(true);
        Reclamation saved = reclamationRepository.save(r);
        historiqueService.enregistrer(expediteur, "RECLAMATION_CREE", "Réclamation " + saved.getId() + " créée");
        return saved;
    }

    public Reclamation changerStatut(Long reclamationId, StatutReclamation nouveauStatut, String commentaire) {
        log.info("Changement statut réclamation {}: {}", reclamationId, nouveauStatut);
        Reclamation reclamation = reclamationRepository.findById(reclamationId)
                .orElseThrow(() -> new IllegalArgumentException("Réclamation introuvable: " + reclamationId));

        reclamation.setStatut(nouveauStatut.name());
        if (commentaire != null && !commentaire.trim().isEmpty()) {
            reclamation.setCommentaire(commentaire);
        }

        Reclamation saved = reclamationRepository.save(reclamation);
        historiqueService.enregistrer(reclamation.getExpediteur(), "RECLAMATION_STATUT_CHANGE",
                "Réclamation " + reclamationId + " -> " + nouveauStatut);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Reclamation> listerParExpediteur(Long expediteurId, Pageable pageable) {
        return reclamationRepository.findByExpediteurIdOrderByDateCreationDesc(expediteurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Reclamation> rechercher(ReclamationRechercheDTO criteres, Pageable pageable) {
        if (criteres.getExpediteurId() != null && criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            return reclamationRepository.findByExpediteurEtDateRange(criteres.getExpediteurId(),
                    criteres.getDateDebut(), criteres.getDateFin(), pageable);
        }
        if (criteres.getExpediteurId() != null && criteres.getStatut() != null) {
            return reclamationRepository.findByExpediteurEtStatut(criteres.getExpediteurId(),
                    criteres.getStatut().name(), pageable);
        }
        if (criteres.getExpediteurId() != null && criteres.getType() != null) {
            return reclamationRepository.findByExpediteurEtType(criteres.getExpediteurId(), criteres.getType().name(),
                    pageable);
        }
        if (criteres.getExpediteurId() != null && criteres.getQuery() != null
                && !criteres.getQuery().trim().isEmpty()) {
            return reclamationRepository.rechercherParExpediteur(criteres.getExpediteurId(), criteres.getQuery(),
                    pageable);
        }
        if (criteres.getColisId() != null) {
            return reclamationRepository.findByColisIdOrderByDateCreationDesc(criteres.getColisId(), pageable);
        }
        if (criteres.getStatut() != null) {
            return reclamationRepository.findByStatut(criteres.getStatut().name(), pageable);
        }
        if (criteres.getType() != null) {
            return reclamationRepository.findByType(criteres.getType().name(), pageable);
        }
        if (criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            return reclamationRepository.findByDateRange(criteres.getDateDebut(), criteres.getDateFin(), pageable);
        }
        return reclamationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long countParExpediteurEtStatut(Long expediteurId, StatutReclamation statut) {
        return reclamationRepository.countParExpediteurEtStatut(expediteurId, statut.name());
    }

    @Transactional(readOnly = true)
    public long countParExpediteurEtType(Long expediteurId, TypeReclamation type) {
        return reclamationRepository.countParExpediteurEtType(expediteurId, type.name());
    }

    @Transactional(readOnly = true)
    public List<ReclamationStatistiquesDTO> statistiquesParStatut(Long expediteurId) {
        List<Object[]> results = reclamationRepository.statistiquesParStatut(expediteurId);
        return results.stream().map(row -> {
            ReclamationStatistiquesDTO stats = new ReclamationStatistiquesDTO();
            stats.setCategorie("statut");
            stats.setValeur((String) row[0]);
            stats.setNombre((Long) row[1]);
            return stats;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReclamationStatistiquesDTO> statistiquesParType(Long expediteurId) {
        List<Object[]> results = reclamationRepository.statistiquesParType(expediteurId);
        return results.stream().map(row -> {
            ReclamationStatistiquesDTO stats = new ReclamationStatistiquesDTO();
            stats.setCategorie("type");
            stats.setValeur((String) row[0]);
            stats.setNombre((Long) row[1]);
            return stats;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Reclamation> reclamationsActivesParColis(Long colisId) {
        return reclamationRepository.findReclamationsActivesParColis(colisId);
    }

    @Transactional
    public int nettoyerEnAttenteAnciennes(int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        List<Reclamation> anciennes = reclamationRepository.findEnAttenteAnciennes(dateLimite);
        reclamationRepository.deleteAll(anciennes);
        return anciennes.size();
    }
}
