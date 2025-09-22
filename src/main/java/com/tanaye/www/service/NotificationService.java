package com.tanaye.www.service;

import com.tanaye.www.dto.NotificationRechercheDTO;
import com.tanaye.www.entity.Notification;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.TypeNotification;
import com.tanaye.www.repository.NotificationRepository;
import com.tanaye.www.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification notifier(Utilisateur destinataire, String titre, String contenu) {
        return notifier(destinataire, titre, contenu, TypeNotification.SYSTEME);
    }

    @Transactional
    public Notification notifier(Utilisateur destinataire, String titre, String contenu, TypeNotification type) {
        log.info("Notification {} pour utilisateur {}: {}", type, destinataire.getId(), titre);
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setTitre(titre);
        notification.setContenu(contenu);
        notification.setType(type.name());
        notification.setDateCreation(LocalDateTime.now());
        notification.setEstLu(false);
        notification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + destinataire.getId(), notification);
        return notification;
    }

    @Transactional(readOnly = true)
    public Page<Notification> lister(Long userId, Pageable pageable) {
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Notification> notificationsNonLues(Long userId, Pageable pageable) {
        return notificationRepository.findNonLues(userId, pageable);
    }

    @Transactional
    public int marquerToutesCommeLues(Long userId) {
        return notificationRepository.marquerToutesCommeLues(userId);
    }

    @Transactional
    public int marquerCommeLue(Long notificationId, Long userId) {
        return notificationRepository.marquerCommeLue(notificationId, userId);
    }

    @Transactional(readOnly = true)
    public Page<Notification> rechercher(NotificationRechercheDTO criteres, Pageable pageable) {
        if (criteres.getQuery() != null && !criteres.getQuery().trim().isEmpty()) {
            return notificationRepository.rechercher(criteres.getUserId(), criteres.getQuery(), pageable);
        }
        if (criteres.getType() != null) {
            return notificationRepository.findByType(criteres.getUserId(), criteres.getType().name(), pageable);
        }
        if (criteres.getDateDebut() != null && criteres.getDateFin() != null) {
            return notificationRepository.findByDateRange(criteres.getUserId(), criteres.getDateDebut(),
                    criteres.getDateFin(), pageable);
        }
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(criteres.getUserId(), pageable);
    }

    @Transactional(readOnly = true)
    public long countNonLues(Long userId) {
        return notificationRepository.countByDestinataireIdAndEstLuFalse(userId);
    }

    @Transactional(readOnly = true)
    public long countNonLuesParType(Long userId, TypeNotification type) {
        return notificationRepository.countNonLuesParType(userId, type.name());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> statistiquesParType(Long userId) {
        List<Object[]> results = notificationRepository.statistiquesParType(userId);
        return results.stream().collect(java.util.stream.Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]));
    }

    @Transactional
    public int nettoyerAnciennes(Long userId, int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        return notificationRepository.supprimerAnciennes(userId, dateLimite);
    }

    @Transactional(readOnly = true)
    public Utilisateur trouverUtilisateurParId(Long userId) {
        return utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    // ===== MÉTHODES WEBSOCKET INTÉGRÉES =====

    /**
     * Envoie le count des notifications non lues via WebSocket
     */
    public void envoyerCountNotificationsNonLues(Utilisateur utilisateur) {
        String destination = "/topic/notifications/" + utilisateur.getId() + "/count";
        long count = countNonLues(utilisateur.getId());

        log.debug("Envoi count notifications non lues WebSocket à l'utilisateur {}: {}", utilisateur.getId(), count);
        messagingTemplate.convertAndSend(destination, Map.of("count", count));
    }

    /**
     * Notifie un nouveau message reçu (base + WebSocket)
     */
    public void notifierNouveauMessage(Utilisateur destinataire, String expediteurNom, Long messageId) {
        // Créer notification en base
        notifier(destinataire, "Nouveau message",
                "Vous avez reçu un nouveau message de " + expediteurNom, TypeNotification.NOUVEAU_MESSAGE);

        // Envoyer message WebSocket spécifique
        String destination = "/topic/notifications/" + destinataire.getId();
        NotificationMessage message = new NotificationMessage(
                "Nouveau message",
                "Vous avez reçu un nouveau message de " + expediteurNom,
                "NOUVEAU_MESSAGE",
                messageId);

        log.info("Notification nouveau message WebSocket à l'utilisateur {}: {}", destinataire.getId(), expediteurNom);
        messagingTemplate.convertAndSend(destination, message);

        // Mettre à jour le count
        envoyerCountNotificationsNonLues(destinataire);
    }

    /**
     * Notifie l'assignation d'un colis (base + WebSocket)
     */
    public void notifierColisAssigne(Utilisateur destinataire, String colisDescription, Long colisId) {
        // Créer notification en base
        notifier(destinataire, "Colis assigné",
                "Un colis vous a été assigné: " + colisDescription, TypeNotification.COLIS_AFFECTE);

        // Envoyer message WebSocket spécifique
        String destination = "/topic/notifications/" + destinataire.getId();
        NotificationMessage message = new NotificationMessage(
                "Colis assigné",
                "Un colis vous a été assigné: " + colisDescription,
                "COLIS_AFFECTE",
                colisId);

        log.info("Notification colis assigné WebSocket à l'utilisateur {}: {}", destinataire.getId(), colisDescription);
        messagingTemplate.convertAndSend(destination, message);

        // Mettre à jour le count
        envoyerCountNotificationsNonLues(destinataire);
    }

    /**
     * Notifie la confirmation d'un paiement (base + WebSocket)
     */
    public void notifierPaiementConfirme(Utilisateur destinataire, String montant, Long paiementId) {
        // Créer notification en base
        notifier(destinataire, "Paiement confirmé",
                "Votre paiement de " + montant + " a été confirmé", TypeNotification.PAIEMENT_RECU);

        // Envoyer message WebSocket spécifique
        String destination = "/topic/notifications/" + destinataire.getId();
        NotificationMessage message = new NotificationMessage(
                "Paiement confirmé",
                "Votre paiement de " + montant + " a été confirmé",
                "PAIEMENT_RECU",
                paiementId);

        log.info("Notification paiement confirmé WebSocket à l'utilisateur {}: {}", destinataire.getId(), montant);
        messagingTemplate.convertAndSend(destination, message);

        // Mettre à jour le count
        envoyerCountNotificationsNonLues(destinataire);
    }

    /**
     * Classe interne pour les messages de notification WebSocket
     */
    public static class NotificationMessage {
        private final String titre;
        private final String contenu;
        private final String type;
        private final Long referenceId;

        public NotificationMessage(String titre, String contenu, String type, Long referenceId) {
            this.titre = titre;
            this.contenu = contenu;
            this.type = type;
            this.referenceId = referenceId;
        }

        // Getters
        public String getTitre() {
            return titre;
        }

        public String getContenu() {
            return contenu;
        }

        public String getType() {
            return type;
        }

        public Long getReferenceId() {
            return referenceId;
        }
    }
}
