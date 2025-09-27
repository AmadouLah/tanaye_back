package com.tanaye.www.service;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Voyage;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.StatutColis;
import com.tanaye.www.enums.TypeColis;
import com.tanaye.www.repository.ColisRepository;
import com.tanaye.www.repository.VoyageRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ColisService {

    private final ColisRepository colisRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueService historiqueService;
    private final VoyageRepository voyageRepository;
    private final NotificationService notificationService;

    public Colis creer(Long expediteurId, Colis colis) {
        log.info("Création colis par expediteur {}", expediteurId);
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur introuvable: " + expediteurId));

        colis.setExpediteur(expediteur);
        colis.setStatut(StatutColis.EN_ATTENTE);
        Colis saved = colisRepository.save(colis);
        historiqueService.enregistrer(expediteur, "COLIS_CREE", "Colis " + saved.getId() + " créé");
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Colis> listerParExpediteur(Long expediteurId, Pageable pageable) {
        return colisRepository.findByExpediteurIdOrderByDateCreationDesc(expediteurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Colis> rechercher(Long villeDepartId, Long villeArriveeId, Long expediteurId, Long destinataireId,
            Long voyageId, StatutColis statut, TypeColis type, Pageable pageable) {
        if (Objects.nonNull(voyageId))
            return colisRepository.findByVoyageIdOrderByDateCreationDesc(voyageId, pageable);
        if (Objects.nonNull(expediteurId))
            return colisRepository.findByExpediteurIdOrderByDateCreationDesc(expediteurId, pageable);
        if (Objects.nonNull(destinataireId))
            return colisRepository.findByDestinataireIdOrderByDateCreationDesc(destinataireId, pageable);
        if (Objects.nonNull(statut))
            return colisRepository.findByStatutOrderByDateCreationDesc(statut, pageable);
        if (Objects.nonNull(type))
            return colisRepository.findByTypeOrderByDateCreationDesc(type, pageable);
        if (Objects.nonNull(villeDepartId) && Objects.nonNull(villeArriveeId))
            return colisRepository.findByVilleDepartIdAndVilleArriveeIdOrderByDateCreationDesc(villeDepartId,
                    villeArriveeId, pageable);
        return colisRepository.findAll(pageable);
    }

    public Colis changerStatut(Long colisId, StatutColis statut) {
        Colis c = colisRepository.findById(colisId)
                .orElseThrow(() -> new IllegalArgumentException("Colis introuvable: " + colisId));
        c.setStatut(statut);
        return colisRepository.save(c);
    }

    public Colis affecterAuVoyage(Long colisId, Long voyageId) {
        Colis c = colisRepository.findById(colisId)
                .orElseThrow(() -> new IllegalArgumentException("Colis introuvable: " + colisId));
        Voyage v = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new IllegalArgumentException("Voyage introuvable: " + voyageId));

        if (v.getStatut() != com.tanaye.www.enums.StatutVoyage.OUVERT)
            throw new IllegalStateException("Voyage non ouvert");

        Double dejaAlloue = colisRepository.totalPoidsParVoyage(voyageId);
        Double poidsColis = c.getPoids() != null ? c.getPoids() : 0d;
        Double capacite = v.getCapacitePoids() != null ? v.getCapacitePoids() : 0d;
        if (dejaAlloue + poidsColis > capacite)
            throw new IllegalStateException("Capacité du voyage dépassée");

        c.setVoyage(v);
        c.setStatut(StatutColis.EN_COURS);
        Colis saved = colisRepository.save(c);
        historiqueService.enregistrer(c.getExpediteur(), "COLIS_AFFECTE_VOYAGE",
                "Colis " + saved.getId() + " -> voyage " + v.getId());
        if (c.getExpediteur() != null) {
            notificationService.notifier(c.getExpediteur(), "Colis affecté",
                    "Votre colis " + saved.getId() + " a été affecté au voyage " + v.getId());
        }
        if (v.getVoyageur() != null) {
            notificationService.notifier(v.getVoyageur(), "Nouveau colis",
                    "Un colis " + saved.getId() + " a été affecté à votre voyage " + v.getId());
        }
        return saved;
    }
}
