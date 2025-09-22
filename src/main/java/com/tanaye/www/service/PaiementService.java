package com.tanaye.www.service;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Paiement;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.ModePaiement;
import com.tanaye.www.repository.ColisRepository;
import com.tanaye.www.repository.PaiementRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ColisRepository colisRepository;
    private final HistoriqueService historiqueService;
    private final RecuService recuService;

    public Paiement creer(Long payeurId, Long colisId, ModePaiement mode, BigDecimal montant) {
        log.info("Création paiement: payeur={}, colis={}, mode={}, montant={}", payeurId, colisId, mode, montant);
        Utilisateur payeur = utilisateurRepository.findById(payeurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + payeurId));
        Colis colis = (colisId != null) ? colisRepository.findById(colisId).orElse(null) : null;

        Paiement p = new Paiement();
        p.setPayeur(payeur);
        p.setColis(colis);
        p.setMode(mode);
        p.setMontant(montant);
        p.setReussi(false);
        Paiement saved = paiementRepository.save(p);
        historiqueService.enregistrer(payeur, "PAIEMENT_CREE", "Paiement " + saved.getId() + " créé");
        return saved;
    }

    public Paiement confirmerPaiement(Long paiementId, String referenceExterne) {
        Paiement p = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable: " + paiementId));
        p.setReussi(true);
        p.setReferenceExterne(referenceExterne);
        Paiement saved = paiementRepository.save(p);
        historiqueService.enregistrer(p.getPayeur(), "PAIEMENT_CONFIRME", "Paiement " + saved.getId() + " confirmé");
        recuService.genererApresConfirmation(saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Paiement> listerParPayeur(Long payeurId, Pageable pageable) {
        return paiementRepository.findByPayeurIdOrderByDateCreationDesc(payeurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Paiement> listerParMode(ModePaiement mode, Pageable pageable) {
        return paiementRepository.findByMode(mode, pageable);
    }
}
