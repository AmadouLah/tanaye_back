# Système de Notifications WebSocket - Tanaye

## 🚀 Vue d'ensemble

Le système de notifications WebSocket permet d'envoyer des notifications en temps réel aux utilisateurs connectés via WebSocket, en plus de la persistance en base de données.

## 📁 Architecture

### Services

- **`NotificationService`** : Service unifié pour la gestion complète des notifications (base de données + WebSocket)

### Contrôleurs

- **`NotificationController`** : Contrôleur unifié pour les notifications (REST + WebSocket)

## 🔧 Configuration

### WebSocket Config

```java
// Endpoint WebSocket : /ws
// Topics : /topic/notifications/{userId}
// Application destinations : /app/notifications/{userId}/*
```

### Security Config

```java
// WebSocket autorisé sans authentification sur /ws/**
.requestMatchers("/ws/**").permitAll()
```

## 📡 Endpoints WebSocket

### Souscription aux notifications

```javascript
// Se connecter au WebSocket
const socket = new SockJS("/ws");
const stompClient = Stomp.over(socket);

// S'abonner aux notifications d'un utilisateur
stompClient.subscribe("/topic/notifications/123", function (message) {
  const notification = JSON.parse(message.body);
  console.log("Nouvelle notification:", notification);
});

// S'abonner au count des notifications non lues
stompClient.subscribe("/topic/notifications/123/count", function (message) {
  const data = JSON.parse(message.body);
  console.log("Count notifications non lues:", data.count);
});
```

### Actions WebSocket

```javascript
// Marquer une notification comme lue
stompClient.send(
  "/app/notifications/123/marquer-lue",
  {},
  JSON.stringify({ notificationId: 456 })
);

// Marquer toutes les notifications comme lues
stompClient.send("/app/notifications/123/marquer-toutes-lues");

// Demander le count actuel
stompClient.send("/app/notifications/123/demander-count");

// S'abonner aux notifications
stompClient.send("/app/notifications/123/souscrire");
```

## 💻 Utilisation dans les Services

### Exemple : Notifier un nouveau message

```java
@Service
@RequiredArgsConstructor
public class MessageService {

    private final NotificationService notificationService;

    public void envoyerMessage(Utilisateur destinataire, String contenu) {
        // ... logique d'envoi du message ...

        // Notifier via WebSocket + base de données
        notificationService.notifierNouveauMessage(
            destinataire,
            expediteur.getNom(),
            message.getId()
        );
    }
}
```

### Exemple : Notifier l'assignation d'un colis

```java
@Service
@RequiredArgsConstructor
public class ColisService {

    private final NotificationService notificationService;

    public void assignerColis(Colis colis, Utilisateur voyageur) {
        // ... logique d'assignation ...

        // Notifier via WebSocket + base de données
        notificationService.notifierColisAssigne(
            voyageur,
            colis.getDescription(),
            colis.getId()
        );
    }
}
```

## 🎯 Types de Notifications

### Types disponibles

- `NOUVEAU_MESSAGE` : Nouveau message reçu
- `NOUVEAU_COLIS` : Nouveau colis créé
- `COLIS_AFFECTE` : Colis assigné à un voyageur
- `COLIS_LIVRE` : Colis livré
- `PAIEMENT_RECU` : Paiement reçu
- `PAIEMENT_ECHEC` : Échec de paiement
- `RECLAMATION_CREEE` : Nouvelle réclamation
- `INCIDENT_SIGNALE` : Incident signalé
- `AVIS_RECU` : Nouvel avis reçu
- `VOYAGE_PROPOSE` : Voyage proposé
- `VOYAGE_ACCEPTE` : Voyage accepté
- `VOYAGE_REFUSE` : Voyage refusé
- `SYSTEME` : Notification système

## 🔄 Flux de Notification

1. **Événement déclencheur** (nouveau message, colis assigné, etc.)
2. **Création en base** via `NotificationService.notifier()`
3. **Envoi WebSocket** via `WebSocketNotificationService`
4. **Mise à jour du count** des notifications non lues
5. **Réception côté client** via WebSocket
6. **Mise à jour de l'UI** en temps réel

## 🛡️ Sécurité

- **Authentification** : Tous les endpoints WebSocket nécessitent une authentification
- **Autorisation** : Les utilisateurs ne peuvent accéder qu'à leurs propres notifications
- **Validation** : Validation des données côté serveur avant envoi

## 📊 Monitoring

### Logs

```java
// Les services loggent automatiquement :
log.info("Notification {} pour utilisateur {}: {}", type, destinataire.getId(), titre);
log.info("Envoi notification WebSocket à l'utilisateur {}: {}", destinataire.getId(), notification.getTitre());
```

### Métriques

- Nombre de notifications envoyées
- Nombre de connexions WebSocket actives
- Temps de réponse des notifications

## 🚨 Gestion d'Erreurs

### Côté Serveur

```java
try {
    // Envoi notification
} catch (Exception e) {
    log.error("Erreur lors de l'envoi de notification: {}", e.getMessage());
    // Fallback : notification en base seulement
}
```

### Côté Client

```javascript
stompClient.onerror = function (error) {
  console.error("Erreur WebSocket:", error);
  // Fallback : polling REST
};
```

## 🔧 Maintenance

### Nettoyage des anciennes notifications

```java
// Via endpoint REST
DELETE /api/notifications/{userId}/nettoyer?joursRetention=30

// Via service
notificationService.nettoyerAnciennes(userId, 30);
```

### Statistiques

```java
// Statistiques par type
GET /api/notifications/{userId}/statistiques

// Count notifications non lues
GET /api/notifications/{userId}/count/non-lues
```

## 📱 Intégration Frontend

### React/Next.js

```javascript
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

export const useNotifications = (userId) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      // S'abonner aux notifications
      stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
        const notification = JSON.parse(message.body);
        setNotifications((prev) => [notification, ...prev]);
      });

      // S'abonner au count
      stompClient.subscribe(
        `/topic/notifications/${userId}/count`,
        (message) => {
          const data = JSON.parse(message.body);
          setUnreadCount(data.count);
        }
      );
    });

    return () => stompClient.disconnect();
  }, [userId]);

  return { notifications, unreadCount };
};
```

## ✅ Bonnes Pratiques

1. **Toujours utiliser `NotificationService`** pour les notifications complètes (base + WebSocket)
2. **Gérer les erreurs WebSocket** avec des fallbacks REST
3. **Nettoyer régulièrement** les anciennes notifications
4. **Logger les actions importantes** pour le debugging
5. **Tester les connexions WebSocket** en cas de perte de réseau
6. **Utiliser la pagination** pour les listes de notifications
7. **Implémenter la reconnexion automatique** côté client
