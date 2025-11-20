package com.project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	//lấy tất cả các mục trong đơn hàng theo orderId
	List<OrderItem> findByOrderOrderId(Integer orderId);
}
