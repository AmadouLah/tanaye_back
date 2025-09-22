package com.tanaye.www.service;

import com.tanaye.www.dto.PaiementRechercheDTO;
import com.tanaye.www.dto.PaiementStatistiquesDTO;
import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Paiement;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.ModePaiement;
import com.tanaye.www.enums.StatutPaiement;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Page<Paiement> rechercher(PaiementRechercheDTO criteres, Pageable pageable) {
        if (criteres.getPayeurId() != null && criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            return paiementRepository.findByPayeurEtDateRange(criteres.getPayeurId(), criteres.getDateDebut(),
                    criteres.getDateFin(), pageable);
        }
        if (criteres.getPayeurId() != null && criteres.getStatut() != null) {
            boolean reussi = criteres.getStatut() == StatutPaiement.REUSSI;
            return paiementRepository.findByPayeurEtStatut(criteres.getPayeurId(), reussi, pageable);
        }
        if (criteres.getPayeurId() != null && criteres.getMode() != null) {
            return paiementRepository.findByPayeurEtMode(criteres.getPayeurId(), criteres.getMode(), pageable);
        }
        if (criteres.getMontantMin() != null && criteres.getMontantMax() != null) {
            return paiementRepository.findByMontantRange(criteres.getMontantMin(), criteres.getMontantMax(), pageable);
        }
        if (criteres.getColisId() != null) {
            return paiementRepository.findByColisIdOrderByDateCreationDesc(criteres.getColisId(), pageable);
        }
        return paiementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long countReussis(Long userId) {
        return paiementRepository.countReussisParPayeur(userId);
    }

    @Transactional(readOnly = true)
    public long countEchoues(Long userId) {
        return paiementRepository.countEchouesParPayeur(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalMontantReussi(Long userId) {
        return paiementRepository.totalMontantReussiParPayeur(userId);
    }

    @Transactional(readOnly = true)
    public List<PaiementStatistiquesDTO> statistiquesParMode(Long userId) {
        List<Object[]> results = paiementRepository.statistiquesParMode(userId);
        return results.stream().map(row -> {
            PaiementStatistiquesDTO stats = new PaiementStatistiquesDTO();
            stats.setMode((ModePaiement) row[0]);
            stats.setNombrePaiements((Long) row[1]);
            stats.setMontantTotal((BigDecimal) row[2]);
            stats.setMontantMoyen(stats.getMontantTotal().divide(BigDecimal.valueOf(stats.getNombrePaiements())));
            return stats;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Paiement> paiementReussiParColis(Long colisId) {
        return paiementRepository.findPaiementReussiParColis(colisId);
    }

    @Transactional
    public int nettoyerEchouesAnciens(int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        List<Paiement> anciens = paiementRepository.findEchouesAnciens(dateLimite);
        paiementRepository.deleteAll(anciens);
        return anciens.size();
    }
}
