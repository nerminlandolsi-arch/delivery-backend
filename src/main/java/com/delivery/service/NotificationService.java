package com.delivery.service;

import com.delivery.entity.Notification;
import com.delivery.entity.User;
import com.delivery.repository.NotificationRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void envoyerNotification(User destinataire, String titre, String message,
                                    String type, Long referenceId) {
        // Sauvegarder en base
        Notification notif = Notification.builder()
                .destinataire(destinataire)
                .titre(titre)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notif);

        // Envoyer via FCM si le token est disponible
        if (destinataire.getFcmToken() != null && !destinataire.getFcmToken().isBlank()) {
            envoyerPushNotification(destinataire.getFcmToken(), titre, message);
        }
    }

    private void envoyerPushNotification(String fcmToken, String titre, String message) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase not initialized. Skipping push notification.");
            return;
        }
        try {
            Message msg = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(titre)
                            .setBody(message)
                            .build())
                    .putData("titre", titre)
                    .putData("message", message)
                    .build();

            String response = FirebaseMessaging.getInstance().send(msg);
            log.info("FCM notification sent: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM error: {}", e.getMessage());
        }
    }

    public List<Notification> getNotificationsUser(Long userId) {
        return notificationRepository.findByDestinataireIdOrderByCreatedAtDesc(userId);
    }

    public long getNombreNonLues(Long userId) {
        return notificationRepository.countByDestinataireIdAndLue(userId, false);
    }

    public void marquerCommeLue(Long notifId, Long userId) {
        notificationRepository.findById(notifId).ifPresent(notif -> {
            if (notif.getDestinataire().getId().equals(userId)) {
                notif.setLue(true);
                notificationRepository.save(notif);
            }
        });
    }

    public void marquerToutesCommeLues(Long userId) {
        List<Notification> nonLues = notificationRepository
                .findByDestinataireIdAndLue(userId, false);
        nonLues.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(nonLues);
    }
}
