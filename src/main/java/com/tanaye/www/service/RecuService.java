package com.tanaye.www.service;

import com.tanaye.www.dto.RecuDTO;
import com.tanaye.www.entity.Paiement;
import com.tanaye.www.entity.Recu;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.PaiementRepository;
import com.tanaye.www.repository.RecuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecuService {

    private final RecuRepository recuRepository;
    private final PaiementRepository paiementRepository;

    public Recu genererApresConfirmation(Long paiementId) {
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
        recu.setReferenceExterne(paiement.getReferenceExterne());
        recu.setDateEmission(LocalDateTime.now());
        recu.setNumero(genererNumero());
        recu.setContenu(rendreContenu(recu));
        return recuRepository.save(recu);
    }

    @Transactional(readOnly = true)
    public Page<RecuDTO> listerParPayeur(Long payeurId, Pageable pageable) {
        return recuRepository.findByPayeurIdOrderByDateEmissionDesc(payeurId, pageable)
                .map(r -> new RecuDTO(r.getId(), r.getNumero(), r.getPaiement().getId(), r.getPayeur().getId(),
                        r.getBeneficiaire() != null ? r.getBeneficiaire().getId() : null,
                        r.getMontant(), r.getMode().name(), r.getReferenceExterne(), r.getDateEmission()));
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
                "Réf. PSP: " + (r.getReferenceExterne() != null ? r.getReferenceExterne() : "-") + "\n" +
                "Paiement ID: " + r.getPaiement().getId();
    }
}
