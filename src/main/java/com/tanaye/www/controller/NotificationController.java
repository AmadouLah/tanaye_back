package com.tanaye.www.controller;

import com.tanaye.www.entity.Notification;
import com.tanaye.www.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public Page<Notification> list(@PathVariable Long userId, Pageable pageable) {
        return notificationService.lister(userId, pageable);
    }

    @PostMapping("/{id}/lu")
    public void marquerLu(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
    }

    @MessageMapping("notifications/{userId}/lire")
    @PreAuthorize("isAuthenticated()")
    public void marquerLuWs(@DestinationVariable Long userId) {
        // endpoint symbolique si besoin futur
    }
}
