package com.tanaye.www.service;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.StatutVoyage;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VoyageService {

    private final VoyageRepository voyageRepository;
    private final VilleRepository villeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Voyage creer(Long voyageurId, Long villeDepartId, Long villeArriveeId, Voyage v) {
        Utilisateur voyageur = utilisateurRepository.findById(voyageurId)
                .orElseThrow(() -> new IllegalArgumentException("Voyageur introuvable: " + voyageurId));
        Ville depart = villeRepository.findById(villeDepartId)
                .orElseThrow(() -> new IllegalArgumentException("Ville départ introuvable: " + villeDepartId));
        Ville arrivee = villeRepository.findById(villeArriveeId)
                .orElseThrow(() -> new IllegalArgumentException("Ville arrivée introuvable: " + villeArriveeId));
        v.setVoyageur(voyageur);
        v.setVilleDepart(depart);
        v.setVilleArrivee(arrivee);
        v.setStatut(StatutVoyage.OUVERT);
        return voyageRepository.save(v);
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
}
