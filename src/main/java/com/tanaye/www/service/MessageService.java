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
}
