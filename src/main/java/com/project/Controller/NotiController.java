package com.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Entity.Notification;
import com.project.Service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotiController {
	
	@Autowired
	private NotificationService notificationService;
	
	//lấy tất cả thông bao 
	@GetMapping
	public ResponseEntity<Iterable<Notification>> getAllNotifications() {
		return ResponseEntity.ok(notificationService.getAllNotifications());
	}
	
	//lấy tất cả thông báo theo userId
	@GetMapping("/user/{userId}")
	public ResponseEntity<Iterable<Notification>> getAllNotificationsByUserId(@PathVariable Integer userId) {
		return ResponseEntity.ok(notificationService.getAllNotificationsByUserId(userId));
	}
	
	//xoá thông báo theo notificationId
	@DeleteMapping("/delete/{notificationId}")
	public ResponseEntity<Void> deleteNotificationById(@PathVariable Integer notificationId) {
		notificationService.deleteNotificationById(notificationId);
		return ResponseEntity.ok().build();
	}
	
	//xoá thông báo theo userId
	@DeleteMapping("/delete/user/{userId}")
	public ResponseEntity<Void> deleteNotificationsByUserId(@PathVariable Integer userId) {
		notificationService.deleteNotificationsByUserId(userId);
		return ResponseEntity.ok().build();
	}
	
	//đã đọc thoong báo 
	@GetMapping("/read/{notificationId}")
	public ResponseEntity<Void> markNotificationAsRead(@PathVariable Integer notificationId) {
		notificationService.markNotificationAsRead(notificationId);
		return ResponseEntity.ok().build();
	}
	

}
