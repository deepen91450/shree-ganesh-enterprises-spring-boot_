package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 🔔 Get unread notifications (latest first)
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
}