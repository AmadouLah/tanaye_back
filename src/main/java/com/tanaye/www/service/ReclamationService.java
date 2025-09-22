package com.tanaye.www.service;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Reclamation;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.ColisRepository;
import com.tanaye.www.repository.ReclamationRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Création réclamation: expediteur={}, colis={}", expediteurId, colisId);
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
        r.setAccepteCgu(true);
        Reclamation saved = reclamationRepository.save(r);
        historiqueService.enregistrer(expediteur, "RECLAMATION_CREE", "Réclamation " + saved.getId() + " créée");
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Reclamation> listerParExpediteur(Long expediteurId, Pageable pageable) {
        return reclamationRepository.findByExpediteurIdOrderByDateCreationDesc(expediteurId, pageable);
    }
}
