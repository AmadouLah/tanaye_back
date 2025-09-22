package com.tanaye.www.service;

import com.tanaye.www.dto.VoyageRechercheDTO;
import com.tanaye.www.dto.VoyageStatistiquesDTO;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.ModeTransport;
import com.tanaye.www.enums.StatutVoyage;
import com.tanaye.www.enums.TypeVoyage;
import com.tanaye.www.repository.UtilisateurRepository;
import com.tanaye.www.repository.VilleRepository;
import com.tanaye.www.repository.VoyageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class VoyageService {

    private final VoyageRepository voyageRepository;
    private final VilleRepository villeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Voyage creer(Long voyageurId, Long villeDepartId, Long villeArriveeId, Voyage v) {
        return creer(voyageurId, villeDepartId, villeArriveeId, v, TypeVoyage.OCCASIONNEL, ModeTransport.VOITURE);
    }

    public Voyage creer(Long voyageurId, Long villeDepartId, Long villeArriveeId, Voyage v, TypeVoyage type,
            ModeTransport modeTransport) {
        log.info("Création voyage: {} -> {} par voyageur {}", villeDepartId, villeArriveeId, voyageurId);
        Utilisateur voyageur = utilisateurRepository.findById(voyageurId)
                .orElseThrow(() -> new IllegalArgumentException("Voyageur introuvable: " + voyageurId));
        Ville depart = villeRepository.findById(villeDepartId)
                .orElseThrow(() -> new IllegalArgumentException("Ville départ introuvable: " + villeDepartId));
        Ville arrivee = villeRepository.findById(villeArriveeId)
                .orElseThrow(() -> new IllegalArgumentException("Ville arrivée introuvable: " + villeArriveeId));
        v.setVoyageur(voyageur);
        v.setVilleDepart(depart);
        v.setVilleArrivee(arrivee);
        v.setType(type.name());
        v.setModeTransport(modeTransport.name());
        v.setStatut(StatutVoyage.OUVERT);
        return voyageRepository.save(v);
    }

    public Voyage changerStatut(Long voyageId, StatutVoyage nouveauStatut) {
        log.info("Changement statut voyage {}: {}", voyageId, nouveauStatut);
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new IllegalArgumentException("Voyage introuvable: " + voyageId));
        voyage.setStatut(nouveauStatut);
        return voyageRepository.save(voyage);
    }

    @Transactional(readOnly = true)
    public Page<Voyage> listerParTrajet(Long villeDepartId, Long villeArriveeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voyageRepository.findByVilleDepartIdAndVilleArriveeIdOrderByDateDepartAsc(villeDepartId, villeArriveeId,
                pageable);
    }

    @Transactional(readOnly = true)
    public Page<Voyage> listerParStatut(StatutVoyage statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voyageRepository.findByStatutOrderByDateDepartAsc(statut, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Voyage> listerParVoyageur(Long voyageurId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voyageRepository.findByVoyageurIdOrderByDateDepartDesc(voyageurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Voyage> rechercher(VoyageRechercheDTO criteres, Pageable pageable) {
        if (criteres.getVoyageurId() != null && criteres.getDateDepartDebut() != null
                && criteres.getDateDepartFin() != null) {
            return voyageRepository.findByVoyageurEtDateRange(criteres.getVoyageurId(), criteres.getDateDepartDebut(),
                    criteres.getDateDepartFin(), pageable);
        }
        if (criteres.getVoyageurId() != null && criteres.getStatut() != null) {
            return voyageRepository.findByVoyageurEtStatut(criteres.getVoyageurId(), criteres.getStatut(), pageable);
        }
        if (criteres.getVilleDepartId() != null && criteres.getStatut() != null) {
            return voyageRepository.findByVilleDepartEtStatut(criteres.getVilleDepartId(), criteres.getStatut(),
                    pageable);
        }
        if (criteres.getVilleArriveeId() != null && criteres.getStatut() != null) {
            return voyageRepository.findByVilleArriveeEtStatut(criteres.getVilleArriveeId(), criteres.getStatut(),
                    pageable);
        }
        if (criteres.getVilleDepartId() != null && criteres.getVilleArriveeId() != null
                && criteres.getStatut() != null) {
            return voyageRepository.findByTrajetEtStatut(criteres.getVilleDepartId(), criteres.getVilleArriveeId(),
                    criteres.getStatut(), pageable);
        }
        if (criteres.getDateDepartDebut() != null && criteres.getDateDepartFin() != null
                && criteres.getStatut() != null) {
            return voyageRepository.findByDateRangeEtStatut(criteres.getDateDepartDebut(), criteres.getDateDepartFin(),
                    criteres.getStatut(), pageable);
        }
        if (criteres.getPrixMin() != null && criteres.getPrixMax() != null && criteres.getStatut() != null) {
            return voyageRepository.findByPrixRangeEtStatut(criteres.getPrixMin(), criteres.getPrixMax(),
                    criteres.getStatut(), pageable);
        }
        if (criteres.getPoidsMin() != null && criteres.getStatut() != null) {
            return voyageRepository.findByPoidsMinEtStatut(criteres.getPoidsMin(), criteres.getStatut(), pageable);
        }
        if (criteres.getPaysId() != null && criteres.getStatut() != null) {
            return voyageRepository.findByPaysEtStatut(criteres.getPaysId(), criteres.getStatut(), pageable);
        }
        if (criteres.getContinent() != null && criteres.getStatut() != null) {
            return voyageRepository.findByContinentEtStatut(criteres.getContinent(), criteres.getStatut(), pageable);
        }
        return voyageRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Voyage> futursParVoyageur(Long voyageurId) {
        return voyageRepository.findFutursParVoyageur(voyageurId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Voyage> passesParVoyageur(Long voyageurId) {
        return voyageRepository.findPassesParVoyageur(voyageurId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long countParVoyageurEtStatut(Long voyageurId, StatutVoyage statut) {
        return voyageRepository.countParVoyageurEtStatut(voyageurId, statut);
    }

    @Transactional(readOnly = true)
    public List<VoyageStatistiquesDTO> statistiquesParStatut(Long voyageurId) {
        List<Object[]> results = voyageRepository.statistiquesParStatut(voyageurId);
        long total = results.stream().mapToLong(row -> (Long) row[1]).sum();
        return results.stream().map(row -> {
            VoyageStatistiquesDTO stats = new VoyageStatistiquesDTO();
            stats.setStatut((StatutVoyage) row[0]);
            stats.setNombreVoyages((Long) row[1]);
            stats.setPourcentage(total > 0 ? (double) stats.getNombreVoyages() / total * 100 : 0);
            return stats;
        }).collect(Collectors.toList());
    }

    @Transactional
    public int nettoyerPlanifiesDepasses() {
        List<Voyage> depasses = voyageRepository.findPlanifiesDepasses(LocalDateTime.now());
        voyageRepository.deleteAll(depasses);
        return depasses.size();
    }

    @Transactional
    public int marquerEnRetard() {
        List<Voyage> enRetard = voyageRepository.findEnCoursEnRetard(LocalDateTime.now());
        enRetard.forEach(v -> v.setStatut(StatutVoyage.EN_RETARD));
        voyageRepository.saveAll(enRetard);
        return enRetard.size();
    }
}
