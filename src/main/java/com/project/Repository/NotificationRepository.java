package com.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
