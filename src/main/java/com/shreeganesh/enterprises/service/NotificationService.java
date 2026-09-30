package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.Notification;
import com.shreeganesh.enterprises.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    // 🔔 Create Notification
    public void create(String message) {
        Notification n = new Notification();
        n.setMessage(message);
        n.setRead(false); // mark as unread
        repo.save(n);
    }

    // 🔔 Get Unread Notifications
    public List<Notification> getUnread() {
        return repo.findByIsReadFalseOrderByCreatedAtDesc();
    }

    // ✅ Mark All Notifications as Read
    public void markAllAsRead() {
        List<Notification> list = repo.findByIsReadFalseOrderByCreatedAtDesc();

        for (Notification n : list) {
            n.setRead(true);
        }

        repo.saveAll(list);
    }
}