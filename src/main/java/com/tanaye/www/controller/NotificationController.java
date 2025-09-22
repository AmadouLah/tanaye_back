package com.tanaye.www.controller;

import com.tanaye.www.dto.NotificationRechercheDTO;
import com.tanaye.www.entity.Notification;
import com.tanaye.www.enums.TypeNotification;
import com.tanaye.www.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    @Operation(summary = "Liste des notifications d'un utilisateur")
    public ResponseEntity<Page<Notification>> list(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(notificationService.lister(userId, pageable));
    }

    @GetMapping("/{userId}/non-lues")
    @Operation(summary = "Liste des notifications non lues d'un utilisateur")
    public ResponseEntity<Page<Notification>> nonLues(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(notificationService.notificationsNonLues(userId, pageable));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de notifications")
    public ResponseEntity<Page<Notification>> rechercher(@RequestBody NotificationRechercheDTO criteres,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.rechercher(criteres, pageable));
    }

    @PostMapping("/{id}/lu")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Void> marquerLu(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.marquerCommeLue(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/marquer-toutes-lues")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<Integer> marquerToutesCommeLues(@PathVariable Long userId) {
        int count = notificationService.marquerToutesCommeLues(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{userId}/count/non-lues")
    @Operation(summary = "Compter les notifications non lues")
    public ResponseEntity<Long> countNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countNonLues(userId));
    }

    @GetMapping("/{userId}/count/non-lues/{type}")
    @Operation(summary = "Compter les notifications non lues par type")
    public ResponseEntity<Long> countNonLuesParType(@PathVariable Long userId, @PathVariable TypeNotification type) {
        return ResponseEntity.ok(notificationService.countNonLuesParType(userId, type));
    }

    @GetMapping("/{userId}/statistiques")
    @Operation(summary = "Statistiques des notifications par type")
    public ResponseEntity<Map<String, Long>> statistiquesParType(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.statistiquesParType(userId));
    }

    @DeleteMapping("/{userId}/nettoyer")
    @Operation(summary = "Nettoyer les anciennes notifications")
    public ResponseEntity<Integer> nettoyerAnciennes(@PathVariable Long userId,
            @RequestParam(defaultValue = "30") int joursRetention) {
        int count = notificationService.nettoyerAnciennes(userId, joursRetention);
        return ResponseEntity.ok(count);
    }

    @MessageMapping("notifications/{userId}/lire")
    @PreAuthorize("isAuthenticated()")
    public void marquerLuWs(@DestinationVariable Long userId) {
        // endpoint symbolique si besoin futur
    }
}
