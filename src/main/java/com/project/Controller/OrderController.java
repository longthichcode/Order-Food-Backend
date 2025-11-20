package com.project.Controller;

import com.project.Entity.Order;
import com.project.Service.OrderService;
import com.project.Service.PaymentService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private PaymentService paymentService;

	public static class CreateOrderRequest {
		public Integer userId;
		public Integer tableId;
		public String guestName;
		public String guestPhone;
		public String promoCode;
		public String address;
		public Order.PaymentMethod paymentMethod;
	}

	// Lấy tất cả đơn hàng
	@GetMapping
	public ResponseEntity<Iterable<Order>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	// Tạo đơn hàng từ giỏ hàng
	@PostMapping("/create")
	public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
		Order order = orderService.createOrderFromCart(request.userId, request.tableId, request.guestName,
				request.guestPhone, request.promoCode, request.address, request.paymentMethod);
		return ResponseEntity.ok(order);
	}

	// Hoàn thành đơn hàng
	@PutMapping("/complete/{orderId}")
	public ResponseEntity<Void> completeOrder(@PathVariable Integer orderId) {
		orderService.completeOrder(orderId);
		return ResponseEntity.ok().build();
	}

	// Lấy đơn hàng theo ID
	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable Integer orderId) {
		Order order = orderService.getOrderById(orderId);
		if (order != null) {
			return ResponseEntity.ok(order);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Lấy các mục trong đơn hàng theo ID đơn hàng
	@GetMapping("/{orderId}/items")
	public ResponseEntity<?> getOrderItemsByOrderId(@PathVariable Integer orderId) {
		return ResponseEntity.ok(orderService.getOrderItemsByOrderId(orderId));
	}

	// Cập nhật trạng thái đơn hàng
	@PutMapping("/{orderId}/status")
	public ResponseEntity<Void> updateOrderStatus(@PathVariable Integer orderId,
			@RequestBody Map<String, String> body) {
		String status = body.get("status");
		orderService.updateOrderStatus(orderId, status);
		return ResponseEntity.ok().build();
	}

	// Lọc đơn hàng theo khoảng thời gian
	@GetMapping("/filter")
	public ResponseEntity<Iterable<Order>> filterOrdersByDateRange(
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
		Iterable<Order> orders = orderService.getOrdersByCreatedAtBetween(start, end);
		return ResponseEntity.ok(orders);
	}
	

	// Xử lý callback thanh toán thành công
	@GetMapping("/payment/success")
	public ResponseEntity<Map<String, Object>> paymentSuccess(@RequestParam("orderCode") Integer orderId) {
		try {
			orderService.updateOrderStatus(orderId, Order.Status.PROCESSING.name());
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Thanh toán thành công! Đơn hàng: " + orderId);
			response.put("orderId", orderId);
			response.put("status", Order.Status.PROCESSING.name());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Lỗi khi xử lý thanh toán: " + e.getMessage());
			errorResponse.put("orderId", orderId);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Xử lý callback thanh toán bị hủy
	@GetMapping("/payment/cancel")
	public ResponseEntity<Map<String, Object>> paymentCancel(@RequestParam("orderCode") Integer orderId) {
		try {
			orderService.updateOrderStatus(orderId, Order.Status.PENDING.name());
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Thanh toán bị hủy. Đơn hàng: " + orderId);
			response.put("orderId", orderId);
			response.put("status", Order.Status.PENDING.name());
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Lỗi khi xử lý hủy thanh toán: " + e.getMessage());
			errorResponse.put("orderId", orderId);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

    // Kiểm tra trạng thái thanh toán (dùng API mới)
    @GetMapping("/{orderId}/payment/status")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable Long orderId) {
        try {
            Map<String, Object> info = paymentService.getPaymentInfo(orderId);

                Map<String, Object> response = new HashMap<>();
                response.put("orderId", orderId);
                response.put("status", info.get("status"));
                response.put("amountPaid", info.get("amountPaid"));
                response.put("amountRemaining", info.get("amountRemaining"));

                // Tự động cập nhật trạng thái đơn hàng nếu đã thanh toán
                if ("PAID".equals(info.get("status"))) {
                    orderService.updateOrderPaymentStatus(orderId.intValue(), Order.PaymentStatus.PAID.name());
                    System.out.println("Cập nhật trạng thái đơn hàng " + orderId + " thành PROCESSING do đã thanh toán.");
                }

                //ccaapj nhật trạng thái đơn hàng nếu bị hủy
                if ("CANCELLED".equals(info.get("status"))) {
					orderService.updateOrderPaymentStatus(orderId.intValue(), Order.PaymentStatus.CANCELLED.name());
					System.out.println("Cập nhật trạng thái đơn hàng " + orderId + " thành CANCELLED do thanh toán bị hủy.");
				}
                return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi kiểm tra thanh toán: " + e.getMessage()));
        }
    }

	// Lấy đơn hàng theo user ID
	@GetMapping("/user/{userId}")
	public ResponseEntity<Iterable<Order>> getOrdersByUserId(@PathVariable Integer userId) {
		Iterable<Order> orders = orderService.getOrdersByUserId(userId);
		return ResponseEntity.ok(orders);
	}
	
}