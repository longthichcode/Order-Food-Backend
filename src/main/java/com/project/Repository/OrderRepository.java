package com.project.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.Entity.Order;

import jakarta.transaction.Transactional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	
	//sửa trạng thái của đơn hàng 
	@Modifying
	@Transactional
	@Query(value = "UPDATE orders SET status = :status WHERE order_id = :orderId", nativeQuery = true)
	void updateOrderStatus(@Param("orderId") Integer orderId, @Param("status") String status);
	
	//cập nhật trạng thái thanh toán
	@Modifying
	@Transactional
	@Query(value = "UPDATE orders SET payment_status = :paymentStatus WHERE order_id = :orderId", nativeQuery = true)
	void updatePaymentStatus(@Param("orderId") Integer orderId, @Param("paymentStatus") String paymentStatus);
	
	@Query(value = "SELECT * FROM orders WHERE DATE(created_at) BETWEEN :start AND :end", nativeQuery = true)
	List<Order> findByCreatedAtBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
	
	//lấy tất cả đơn hàng theo user id 
	@Query(value = "SELECT * FROM orders WHERE user_id = :userId", nativeQuery = true)
	List<Order> findByUserId(@Param("userId") Integer userId);
}
