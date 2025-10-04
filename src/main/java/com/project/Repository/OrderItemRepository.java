package com.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
