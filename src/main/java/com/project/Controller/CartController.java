package com.project.Controller;

import com.project.DTO.CartDTO;
import com.project.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	// Xem giỏ hàng
	@GetMapping("/{userId}")
	public ResponseEntity<CartDTO> viewCart(@PathVariable Integer userId) {
		return ResponseEntity.ok(cartService.getCart(userId));
	}

	// Thêm món ăn vào giỏ hàng
	@PostMapping("/{userId}/add")
	public ResponseEntity<CartDTO> addToCart(@PathVariable Integer userId, @RequestParam Integer foodId,
			@RequestParam int quantity, @RequestParam(required = false) String note) {
		try {
			return ResponseEntity.ok(cartService.addToCart(userId, foodId, quantity, note));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Cập nhật số lượng món ăn
	@PutMapping("/{userId}/update")
	public ResponseEntity<CartDTO> updateCart(@PathVariable Integer userId, @RequestParam Integer foodId,
			@RequestParam int quantity) {
		try {
			return ResponseEntity.ok(cartService.updateCart(userId, foodId, quantity));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Sửa ghi chú món ăn trong giỏ hàng
	@PutMapping("/{userId}/update-note")
	public ResponseEntity<CartDTO> updateCartItemNote(@PathVariable Integer userId, @RequestParam Integer foodId,
			@RequestParam String note) {
		try {
			return ResponseEntity.ok(cartService.updateNoteInCart(userId, foodId, note));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Xóa món ăn khỏi giỏ hàng
	@DeleteMapping("/{userId}/remove/{foodId}")
	public ResponseEntity<CartDTO> removeFromCart(@PathVariable Integer userId, @PathVariable Integer foodId) {
		try {
			return ResponseEntity.ok(cartService.removeFromCart(userId, foodId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Áp dụng mã khuyến mãi
	@PostMapping("/{userId}/apply-promotion")
	public ResponseEntity<CartDTO> applyPromotion(@PathVariable Integer userId, @RequestParam String promoCode) {
		try {
			return ResponseEntity.ok(cartService.applyPromotion(userId, promoCode));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Xóa toàn bộ giỏ hàng
	@DeleteMapping("/{userId}/clear")
	public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
		try {
			cartService.clearCart(userId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}