package com.tanaye.www.service;

import com.tanaye.www.entity.Localisation;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.LocalisationRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocalisationService {

    private final LocalisationRepository localisationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Localisation enregistrer(Long utilisateurId, Double lat, Double lng) {
        log.info("Localisation: user={}, lat={}, lng={}", utilisateurId, lat, lng);
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + utilisateurId));
        Localisation loc = new Localisation();
        loc.setUtilisateur(utilisateur);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        loc.setTimestamp(LocalDateTime.now());
        return localisationRepository.save(loc);
    }

    @Transactional(readOnly = true)
    public Page<Localisation> historique(Long utilisateurId, Pageable pageable) {
        return localisationRepository.findByUtilisateurIdOrderByTimestampDesc(utilisateurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Localisation> historiqueEntre(Long utilisateurId, java.time.LocalDateTime debut, java.time.LocalDateTime fin, Pageable pageable) {
        return localisationRepository.findByUtilisateurIdAndTimestampBetweenOrderByTimestampDesc(utilisateurId, debut, fin, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Localisation> rechercheBoite(java.time.LocalDateTime debut, java.time.LocalDateTime fin,
                                             Double minLat, Double maxLat, Double minLng, Double maxLng, Pageable pageable) {
        return localisationRepository.findInBoundingBox(debut, fin, minLat, maxLat, minLng, maxLng, pageable);
    }
}
