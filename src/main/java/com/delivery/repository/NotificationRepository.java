package com.delivery.repository;

import com.delivery.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByDestinataireIdAndLue(Long userId, boolean lue);
    long countByDestinataireIdAndLue(Long userId, boolean lue);
}
