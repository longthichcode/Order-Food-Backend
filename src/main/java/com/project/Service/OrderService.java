package com.project.Service;

import com.project.DTO.CartDTO;
import com.project.DTO.CartItemDTO;
import com.project.Entity.*;
import com.project.Repository.FoodRepository;
import com.project.Repository.OrderItemRepository;
import com.project.Repository.OrderRepository;
import com.project.Repository.PromotionRepository;
import com.project.Repository.SalesReportRepository;
import com.project.Repository.TablesRepository;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private PromotionRepository promorionRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private TablesRepository tablesRepository;

	@Autowired
	private SalesReportRepository salesReportRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Transactional
	public Order createOrderFromCart(Integer userId, Integer tableId, String guestName, String guestPhone,
			String promoCode) {
		// Lấy giỏ hàng từ DB
		CartDTO cartDTO = cartService.getCart(userId);
		if (cartDTO.getCartItems().isEmpty()) {
			throw new IllegalStateException("Cart is empty");
		}

		// Kiểm tra và cập nhật trạng thái bàn
		Tables table = null;
		if (tableId != null) {
			table = tablesRepository.findById(tableId)
					.orElseThrow(() -> new IllegalArgumentException("Table not found"));
			if (table.getStatus() != Tables.Status.AVAILABLE) {
				throw new IllegalStateException("Table is already occupied");
			}
			table.setStatus(Tables.Status.OCCUPIED);
			tablesRepository.save(table);
		}

		// Tạo đơn hàng
		Order order = new Order();
		order.setUser(userService.getUserById(userId));
		order.setTable(table);
		order.setGuestName(guestName);
		order.setGuestPhone(guestPhone);
		order.setTotalPrice(cartDTO.getTotalPrice());
		order.setStatus(Order.Status.PENDING);
		order.setPaymentMethod(Order.PaymentMethod.CASH);
		order.setPaymentStatus(Order.PaymentStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());

		// Áp dụng khuyến mãi
		if (promoCode != null && !promoCode.isEmpty()) {
			cartService.applyPromotion(userId, promoCode);
			order.setPromotion(promorionRepository.findByCode(cartDTO.getPromoCode()).orElse(null));
		}

		// Lưu đơn hàng
		order = orderRepository.save(order);

		// Tạo OrderItem từ CartItemDTO
		for (CartItemDTO cartItemDTO : cartDTO.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setFood(foodRepository.findById(cartItemDTO.getFoodId())
					.orElseThrow(() -> new IllegalArgumentException("Food not found")));
			orderItem.setQuantity(cartItemDTO.getQuantity());
			orderItem.setPrice(cartItemDTO.getPrice());
			orderItem.setNote(cartItemDTO.getNote());
			orderItemRepository.save(orderItem);
		}

		// Gửi thông báo
		String message = "New order created with ID: " + order.getOrderId();
		notificationService.sendNotification(order, message, userId);

		// Xóa giỏ hàng trong DB
		cartService.clearCart(userId);

		return order;
	}

	@Transactional
	public void completeOrder(Integer orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));

		// Cập nhật trạng thái đơn hàng
		order.setStatus(Order.Status.COMPLETED);
		order.setPaymentStatus(Order.PaymentStatus.PAID);
		orderRepository.save(order);

		// Cập nhật trạng thái bàn
		if (order.getTable() != null) {
			Tables table = order.getTable();
			table.setStatus(Tables.Status.AVAILABLE);
			tablesRepository.save(table);
		}

		// Tạo báo cáo doanh thu
		SalesReport salesReport = new SalesReport();
		salesReport.setOrder(order);
		salesReport.setTotalPrice(order.getTotalPrice());
		salesReport.setCreatedAt(LocalDateTime.now());
		salesReportRepository.save(salesReport);

		// Gửi thông báo hoàn tất
		String message = "Order " + order.getOrderId() + " has been completed.";
		notificationService.sendNotification(order, message,
				order.getUser() != null ? order.getUser().getUserId() : null);
	}
}