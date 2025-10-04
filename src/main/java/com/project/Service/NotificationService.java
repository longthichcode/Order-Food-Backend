package com.project.Service;

import com.project.Entity.Notification;
import com.project.Entity.Order;
import com.project.Repository.NotificationRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;

    @Transactional
    public void sendNotification(Order order, String message, Integer userId) {
        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setUser(userService.getUserById(userId));
        notification.setMessage(message);
        notification.setStatus(Notification.Status.UNREAD);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}