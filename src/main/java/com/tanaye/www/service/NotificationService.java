package com.tanaye.www.service;

import com.tanaye.www.entity.Notification;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification notifier(Utilisateur destinataire, String titre, String contenu) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setTitre(titre);
        notification.setContenu(contenu);
        notification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + destinataire.getId(), notification);
        return notification;
    }

    @Transactional(readOnly = true)
    public Page<Notification> lister(Long userId, Pageable pageable) {
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(userId, pageable);
    }

    @Transactional
    public void marquerCommeLue(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setEstLu(true);
            notificationRepository.save(n);
        });
    }
}
