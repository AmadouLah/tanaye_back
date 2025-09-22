package com.tanaye.www.service;

import com.tanaye.www.dto.NotificationRechercheDTO;
import com.tanaye.www.entity.Notification;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.TypeNotification;
import com.tanaye.www.repository.NotificationRepository;
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
}
