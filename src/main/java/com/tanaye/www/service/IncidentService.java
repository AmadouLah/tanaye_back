package com.tanaye.www.service;

import com.tanaye.www.entity.Colis;
import com.tanaye.www.entity.Incident;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.StatutIncident;
import com.tanaye.www.enums.TypeIncident;
import com.tanaye.www.repository.ColisRepository;
import com.tanaye.www.repository.IncidentRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ColisRepository colisRepository;
    private final HistoriqueService historiqueService;

    public Incident creer(Long declencheurId, Long colisId, TypeIncident type, String description) {
        log.info("Signalement incident: declencheur={}, colis={}, type={}", declencheurId, colisId, type);
        Utilisateur user = utilisateurRepository.findById(declencheurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + declencheurId));
        Colis colis = (colisId != null) ? colisRepository.findById(colisId).orElse(null) : null;

        Incident i = new Incident();
        i.setDeclencheur(user);
        i.setColis(colis);
        i.setType(type);
        i.setDescription(description);
        i.setStatut(StatutIncident.OUVERT);
        Incident saved = incidentRepository.save(i);
        historiqueService.enregistrer(user, "INCIDENT_OUVERT", "Incident " + saved.getId() + " ouvert");
        return saved;
    }

    public Incident changerStatut(Long incidentId, StatutIncident statut) {
        Incident i = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident introuvable: " + incidentId));
        i.setStatut(statut);
        Incident saved = incidentRepository.save(i);
        historiqueService.enregistrer(i.getDeclencheur(), "INCIDENT_STATUT_CHANGE",
                "Incident " + saved.getId() + " -> " + statut);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Incident> listerParType(TypeIncident type, Pageable pageable) {
        return incidentRepository.findByTypeOrderByDateCreationDesc(type, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Incident> listerParStatut(StatutIncident statut, Pageable pageable) {
        return incidentRepository.findByStatutOrderByDateCreationDesc(statut, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Incident> rechercher(TypeIncident type, StatutIncident statut, Long userId, Long colisId,
            java.time.LocalDateTime debut, java.time.LocalDateTime fin, Pageable pageable) {
        return incidentRepository.rechercher(type, statut, userId, colisId, debut, fin, pageable);
    }

    @Transactional(readOnly = true)
    public String exporterCsv(TypeIncident type, StatutIncident statut, Long userId, Long colisId,
            java.time.LocalDateTime debut, java.time.LocalDateTime fin) {
        Pageable unpaged = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<Incident> page = incidentRepository.rechercher(type, statut, userId, colisId, debut, fin, unpaged);
        StringBuilder sb = new StringBuilder();
        sb.append("id;dateCreation;declencheurId;colisId;type;statut;description\n");
        page.getContent().forEach(i -> sb
                .append(i.getId()).append(';')
                .append(i.getDateCreation()).append(';')
                .append(i.getDeclencheur() != null ? i.getDeclencheur().getId() : "").append(';')
                .append(i.getColis() != null ? i.getColis().getId() : "").append(';')
                .append(i.getType()).append(';')
                .append(i.getStatut()).append(';')
                .append(echapper(i.getDescription()))
                .append('\n'));
        return sb.toString();
    }

    private String echapper(String v) {
        if (v == null)
            return "";
        String s = v.replace("\n", " ").replace("\r", " ");
        if (s.contains(";") || s.contains("\"")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
