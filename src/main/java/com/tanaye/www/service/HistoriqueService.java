package com.tanaye.www.service;

import com.tanaye.www.entity.Historique;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.HistoriqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;

    public Historique enregistrer(Utilisateur utilisateur, String action, String details) {
        log.info("Historique: {} par utilisateur {}", action, (utilisateur != null ? utilisateur.getId() : null));
        Historique h = new Historique();
        h.setUtilisateur(utilisateur);
        h.setAction(action);
        h.setDetails(details);
        return historiqueRepository.save(h);
    }

    @Transactional(readOnly = true)
    public Page<Historique> listerParUtilisateur(Long utilisateurId, Pageable pageable) {
        return historiqueRepository.findByUtilisateurIdOrderByDateCreationDesc(utilisateurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Historique> rechercher(String q, Pageable pageable) {
        return historiqueRepository.findByActionContainingIgnoreCaseOrderByDateCreationDesc(q, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Historique> rechercherAvancee(Long utilisateurId, String action, java.time.LocalDateTime debut,
            java.time.LocalDateTime fin, Pageable pageable) {
        return historiqueRepository.rechercher(utilisateurId, action, debut, fin, pageable);
    }

    @Transactional(readOnly = true)
    public String exporterCsv(Long utilisateurId, String action, java.time.LocalDateTime debut,
            java.time.LocalDateTime fin) {
        Pageable unpaged = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<Historique> page = historiqueRepository.rechercher(utilisateurId, action, debut, fin, unpaged);
        StringBuilder sb = new StringBuilder();
        sb.append("id;dateCreation;utilisateurId;action;details\n");
        page.getContent().forEach(h -> sb
                .append(h.getId()).append(';')
                .append(h.getDateCreation()).append(';')
                .append(h.getUtilisateur() != null ? h.getUtilisateur().getId() : "").append(';')
                .append(escape(h.getAction())).append(';')
                .append(escape(h.getDetails() != null ? h.getDetails() : ""))
                .append('\n'));
        return sb.toString();
    }

    private String escape(String v) {
        if (v == null)
            return "";
        String s = v.replace("\n", " ").replace("\r", " ");
        if (s.contains(";") || s.contains("\"")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
