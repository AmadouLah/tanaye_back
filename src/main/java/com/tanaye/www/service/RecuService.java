package com.tanaye.www.service;

import com.tanaye.www.dto.RecuDTO;
import com.tanaye.www.dto.RecuRechercheDTO;
import com.tanaye.www.dto.RecuStatistiquesDTO;
import com.tanaye.www.entity.Paiement;
import com.tanaye.www.entity.Recu;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.StatutRecu;
import com.tanaye.www.enums.TypeRecu;
import com.tanaye.www.repository.PaiementRepository;
import com.tanaye.www.repository.RecuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecuService {

    private final RecuRepository recuRepository;
    private final PaiementRepository paiementRepository;

    public Recu genererApresConfirmation(Long paiementId) {
        return genererApresConfirmation(paiementId, TypeRecu.PAIEMENT_COLIS);
    }

    public Recu genererApresConfirmation(Long paiementId, TypeRecu type) {
        log.info("Génération reçu pour paiement {}: type={}", paiementId, type);
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable: " + paiementId));
        if (!Boolean.TRUE.equals(paiement.getReussi())) {
            throw new IllegalStateException("Paiement non confirmé, reçu interdit");
        }
        if (recuRepository.findByPaiementId(paiementId).isPresent()) {
            return recuRepository.findByPaiementId(paiementId).get();
        }

        Utilisateur payeur = paiement.getPayeur();
        Utilisateur beneficiaire = paiement.getColis() != null && paiement.getColis().getVoyage() != null
                ? paiement.getColis().getVoyage().getVoyageur()
                : null;

        Recu recu = new Recu();
        recu.setPaiement(paiement);
        recu.setPayeur(payeur);
        recu.setBeneficiaire(beneficiaire);
        recu.setMontant(paiement.getMontant());
        recu.setMode(paiement.getMode());
        recu.setType(type.name());
        recu.setStatut(StatutRecu.EMIS.name());
        recu.setReferenceExterne(paiement.getReferenceExterne());
        recu.setDateEmission(LocalDateTime.now());
        recu.setNumero(genererNumero());
        recu.setContenu(rendreContenu(recu));
        return recuRepository.save(recu);
    }

    public Recu changerStatut(Long recuId, StatutRecu nouveauStatut) {
        log.info("Changement statut reçu {}: {}", recuId, nouveauStatut);
        Recu recu = recuRepository.findById(recuId)
                .orElseThrow(() -> new IllegalArgumentException("Reçu introuvable: " + recuId));
        recu.setStatut(nouveauStatut.name());
        return recuRepository.save(recu);
    }

    @Transactional(readOnly = true)
    public Page<RecuDTO> listerParPayeur(Long payeurId, Pageable pageable) {
        return recuRepository.findByPayeurIdOrderByDateEmissionDesc(payeurId, pageable)
                .map(r -> new RecuDTO(r.getId(), r.getNumero(), r.getPaiement().getId(), r.getPayeur().getId(),
                        r.getBeneficiaire() != null ? r.getBeneficiaire().getId() : null,
                        r.getMontant(), r.getMode().name(), r.getReferenceExterne(), r.getDateEmission()));
    }

    @Transactional(readOnly = true)
    public Page<Recu> rechercher(RecuRechercheDTO criteres, Pageable pageable) {
        if (criteres.getPayeurId() != null && criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            return recuRepository.findByPayeurEtDateRange(criteres.getPayeurId(), criteres.getDateDebut(),
                    criteres.getDateFin(), pageable);
        }
        if (criteres.getPayeurId() != null && criteres.getMontantMin() != null && criteres.getMontantMax() != null) {
            return recuRepository.findByPayeurEtMontantRange(criteres.getPayeurId(), criteres.getMontantMin(),
                    criteres.getMontantMax(), pageable);
        }
        if (criteres.getPayeurId() != null && criteres.getQuery() != null && !criteres.getQuery().trim().isEmpty()) {
            return recuRepository.rechercherParPayeur(criteres.getPayeurId(), criteres.getQuery(), pageable);
        }
        if (criteres.getPaiementId() != null) {
            Optional<Recu> recuOpt = recuRepository.findByPaiementId(criteres.getPaiementId());
            if (recuOpt.isPresent()) {
                return new org.springframework.data.domain.PageImpl<>(List.of(recuOpt.get()), pageable, 1);
            } else {
                return org.springframework.data.domain.Page.empty();
            }
        }
        if (criteres.getMontantMin() != null) {
            return recuRepository.findByMontantMin(criteres.getMontantMin(), pageable);
        }
        if (criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            List<Recu> resultats = recuRepository.findByDateRange(criteres.getDateDebut(), criteres.getDateFin());
            return new org.springframework.data.domain.PageImpl<>(resultats, pageable, resultats.size());
        }
        return recuRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long countParPayeur(Long payeurId) {
        return recuRepository.countParPayeur(payeurId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalMontantParPayeur(Long payeurId) {
        return recuRepository.totalMontantParPayeur(payeurId);
    }

    @Transactional(readOnly = true)
    public BigDecimal montantMoyenParPayeur(Long payeurId) {
        return recuRepository.montantMoyenParPayeur(payeurId);
    }

    @Transactional(readOnly = true)
    public List<Recu> recentsParPayeur(Long payeurId, int joursRecents) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRecents);
        return recuRepository.findRecentsParPayeur(payeurId, dateLimite);
    }

    @Transactional(readOnly = true)
    public List<Recu> parPayeurEtAnnee(Long payeurId, int annee) {
        return recuRepository.findByPayeurEtAnnee(payeurId, annee);
    }

    @Transactional(readOnly = true)
    public List<RecuStatistiquesDTO> statistiquesParAnnee(Long payeurId) {
        List<Object[]> results = recuRepository.statistiquesParAnnee(payeurId);
        return results.stream().map(row -> {
            RecuStatistiquesDTO stats = new RecuStatistiquesDTO();
            stats.setAnnee((Integer) row[0]);
            stats.setNombreRecus((Long) row[1]);
            stats.setMontantTotal((BigDecimal) row[2]);
            stats.setMontantMoyen(stats.getMontantTotal().divide(BigDecimal.valueOf(stats.getNombreRecus())));
            return stats;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Recu> parPayeurEtPaiement(Long payeurId, Long paiementId) {
        return recuRepository.findByPayeurEtPaiement(payeurId, paiementId);
    }

    @Transactional
    public int nettoyerAnciens(int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        List<Recu> anciens = recuRepository.findAnciens(dateLimite);
        recuRepository.deleteAll(anciens);
        return anciens.size();
    }

    private String genererNumero() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String rand = String.format(Locale.ROOT, "%08d", (int) (Math.random() * 100_000_000));
        return "RC-" + year + "-" + rand;
    }

    private String rendreContenu(Recu r) {
        String beneficiaireTxt = r.getBeneficiaire() != null
                ? (r.getBeneficiaire().getPrenom() + " " + r.getBeneficiaire().getNom())
                : "N/A";
        return "Reçu électronique\n" +
                "Numéro: " + r.getNumero() + "\n" +
                "Date: " + r.getDateEmission() + "\n" +
                "Payeur: " + r.getPayeur().getPrenom() + " " + r.getPayeur().getNom() + "\n" +
                "Bénéficiaire: " + beneficiaireTxt + "\n" +
                "Montant: " + r.getMontant() + "\n" +
                "Mode: " + r.getMode() + "\n" +
                "Type: " + r.getType() + "\n" +
                "Réf. PSP: " + (r.getReferenceExterne() != null ? r.getReferenceExterne() : "-") + "\n" +
                "Paiement ID: " + r.getPaiement().getId();
    }
}
