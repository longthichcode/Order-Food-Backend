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

	// lấy thông báo chưa đọc theo userId
	public Iterable<Notification> getUnreadNotificationsByUserId(Integer userId) {
		return notificationRepository.findAll().stream()
				.filter(n -> n.getUser().getUserId().equals(userId) && n.getStatus() == Notification.Status.UNREAD)
				.toList();
	}

	// đánh dấu thông báo đã đọc
	@Transactional
	public void markNotificationAsRead(Integer notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new IllegalArgumentException("Notification not found"));
		notification.setStatus(Notification.Status.READ);
		notificationRepository.save(notification);
	}

	// xoá thông báo theo userId
	@Transactional
	public void deleteNotificationsByUserId(Integer userId) {
		Iterable<Notification> notifications = notificationRepository.findAll().stream()
				.filter(n -> n.getUser().getUserId().equals(userId)).toList();
		notificationRepository.deleteAll(notifications);
	}

	// xoá thông báo theo notificationId
	@Transactional
	public void deleteNotificationById(Integer notificationId) {
		notificationRepository.deleteById(notificationId);
	}

	// lấy tất cả thông báo
	public Iterable<Notification> getAllNotifications() {
		return notificationRepository.findAll();
	}

	// lấy tất cả thông báo theo userId
	public Iterable<Notification> getAllNotificationsByUserId(Integer userId) {
		return notificationRepository.findAll().stream().filter(n -> n.getUser().getUserId().equals(userId)).toList();
	}

}