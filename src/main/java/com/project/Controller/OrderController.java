package com.project.Controller;

import com.project.Entity.Order;
import com.project.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    public static class CreateOrderRequest {
        public Integer userId;
        public Integer tableId;
        public String guestName;
        public String guestPhone;
        public String promoCode;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrderFromCart(
                request.userId,
                request.tableId,
                request.guestName,
                request.guestPhone,
                request.promoCode
        );
        return ResponseEntity.ok(order);
    }

    @PutMapping("/complete/{orderId}")
    public ResponseEntity<Void> completeOrder(@PathVariable Integer orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
