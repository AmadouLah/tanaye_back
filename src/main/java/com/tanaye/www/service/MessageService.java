package com.tanaye.www.service;

import com.tanaye.www.entity.Message;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.MessageRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    public Message envoyer(Long auteurId, Long destinataireId, String contenu) {
        log.info("Message: {} -> {}", auteurId, destinataireId);
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Auteur introuvable: " + auteurId));
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable: " + destinataireId));
        Message m = new Message();
        m.setAuteur(auteur);
        m.setDestinataire(destinataire);
        m.setContenu(contenu);
        m.setDateEnvoi(LocalDateTime.now());
        Message saved = messageRepository.save(m);
        notificationService.notifier(destinataire, "Nouveau message", contenu);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Message> boiteReception(Long destinataireId, Pageable pageable) {
        return messageRepository.findByDestinataireIdOrderByDateEnvoiDesc(destinataireId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Message> boiteEnvoi(Long auteurId, Pageable pageable) {
        return messageRepository.findByAuteurIdOrderByDateEnvoiDesc(auteurId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Message> conversation(Long utilisateur1, Long utilisateur2, Pageable pageable) {
        return messageRepository.conversation(utilisateur1, utilisateur2, pageable);
    }

    public int marquerConversationLue(Long destinataireId, Long auteurId) {
        return messageRepository.marquerConversationLue(destinataireId, auteurId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> nonLusParInterlocuteur(Long destinataireId) {
        List<MessageRepository.UnreadCountProjection> rows = messageRepository.nonLusParInterlocuteur(destinataireId);
        return rows.stream().collect(Collectors.toMap(MessageRepository.UnreadCountProjection::getInterlocuteurId,
                MessageRepository.UnreadCountProjection::getTotal));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listeThreads(Long userId, String query, int limit) {
        Map<Long, Long> unread = nonLusParInterlocuteur(userId);
        return messageRepository.threads(userId, query).stream()
                .limit(limit)
                .map(t -> {
                    Long interlocuteurId = t.getInterlocuteurId();
                    Page<Message> last = messageRepository.conversation(userId, interlocuteurId,
                            org.springframework.data.domain.PageRequest.of(0, 1));
                    String contenu = last.hasContent() ? last.getContent().get(0).getContenu() : "";
                    String apercu = contenu != null ? contenu.replace('\n', ' ').replace('\r', ' ').trim() : "";
                    if (apercu.length() > 120)
                        apercu = apercu.substring(0, 119) + "\u2026";
                    var userOpt = utilisateurRepository.findById(interlocuteurId);
                    String nom = userOpt.map(u -> u.getNom()).orElse("");
                    String prenom = userOpt.map(u -> u.getPrenom()).orElse("");

                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("interlocuteurId", interlocuteurId);
                    m.put("nom", nom);
                    m.put("prenom", prenom);
                    m.put("lastDate", t.getLastDate());
                    m.put("apercu", apercu);
                    m.put("nonLus", unread.getOrDefault(interlocuteurId, 0L));
                    return m;
                })
                .collect(Collectors.toList());
    }
}
