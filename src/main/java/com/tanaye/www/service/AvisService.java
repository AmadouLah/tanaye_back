package com.tanaye.www.service;

import com.tanaye.www.entity.Avis;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.dto.AvisStatsDTO;
import com.tanaye.www.repository.AvisRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import static com.tanaye.www.repository.AvisRepository.TopVoyageurProjection;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AvisService {

    private final AvisRepository avisRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Avis creer(Long auteurId, Long destinataireId, Integer note, String commentaire) {
        log.info("Création avis: auteur={}, destinataire={}, note={}", auteurId, destinataireId, note);
        if (note == null || note < 1 || note > 5)
            throw new IllegalArgumentException("Note invalide");
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Auteur introuvable: " + auteurId));
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable: " + destinataireId));
        if (avisRepository.existsByAuteurIdAndDestinataireId(auteurId, destinataireId)) {
            throw new IllegalStateException("Vous avez déjà noté cet utilisateur");
        }

        Avis a = new Avis();
        a.setAuteur(auteur);
        a.setDestinataire(destinataire);
        a.setNote(note);
        a.setCommentaire(commentaire);
        return avisRepository.save(a);
    }

    @Transactional(readOnly = true)
    public Page<Avis> listerParDestinataire(Long destinataireId, Pageable pageable) {
        return avisRepository.findByDestinataireIdOrderByDateCreationDesc(destinataireId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Avis> listerParAuteur(Long auteurId, Pageable pageable) {
        return avisRepository.findByAuteurIdOrderByDateCreationDesc(auteurId, pageable);
    }

    @Transactional(readOnly = true)
    public AvisStatsDTO statsPourUtilisateur(Long utilisateurId) {
        Double moyenne = avisRepository.moyenneParUtilisateur(utilisateurId);
        long total = avisRepository.countByDestinataireId(utilisateurId);
        Map<Integer, Long> dist = new LinkedHashMap<>();
        avisRepository.distributionParUtilisateur(utilisateurId)
                .forEach(p -> dist.put(p.getNote(), p.getTotal()));
        return new AvisStatsDTO(utilisateurId, moyenne, total, dist);
    }

    @Transactional(readOnly = true)
    public Page<TopVoyageurProjection> topVoyageursFiables(long minAvis, Pageable pageable) {
        return avisRepository.topVoyageursFiables(minAvis, pageable);
    }

    public Avis mettreAJour(Long avisId, Long auteurId, Integer note, String commentaire) {
        if (note != null && (note < 1 || note > 5))
            throw new IllegalArgumentException("Note invalide");
        Avis avis = avisRepository.findById(avisId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable: " + avisId));
        if (!avis.getAuteur().getId().equals(auteurId)) {
            throw new IllegalStateException("Seul l'auteur peut modifier l'avis");
        }
        if (note != null)
            avis.setNote(note);
        if (commentaire != null)
            avis.setCommentaire(commentaire);
        return avisRepository.save(avis);
    }

    public void supprimer(Long avisId, Long auteurId) {
        Avis avis = avisRepository.findById(avisId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable: " + avisId));
        if (!avis.getAuteur().getId().equals(auteurId)) {
            throw new IllegalStateException("Seul l'auteur peut supprimer l'avis");
        }
        avisRepository.delete(avis);
    }
}
