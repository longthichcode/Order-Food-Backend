package com.project.Service;

import com.project.DTO.CartDTO;
import com.project.DTO.CartItemDTO;
import com.project.Entity.*;
import com.project.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private PromotionRepository promotionRepository;

	// Chuyển đổi Cart sang CartDTO
	private CartDTO toCartDTO(Cart cart) {
		List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
				.map(item -> new CartItemDTO(item.getCartItemId(), item.getFood().getFoodId(), item.getFood().getName(),
						item.getFood().getImageUrl(), item.getQuantity(), item.getPrice(), item.getNote()))
				.collect(Collectors.toList());

		// Tính tổng giá từ các CartItem, đã bao gồm giảm giá 90% nếu isPromotion = true
		BigDecimal totalPrice = cart.getCartItems().stream()
				.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Áp dụng khuyến mãi từ promoCode nếu có
		if (cart.getPromoCode() != null) {
			Promotion promotion = promotionRepository.findByCode(cart.getPromoCode())
					.orElseThrow(() -> new IllegalArgumentException("Invalid promotion code"));
			totalPrice = totalPrice.multiply(BigDecimal.valueOf(1)
					.subtract(BigDecimal.valueOf(promotion.getDiscountPercent()).divide(BigDecimal.valueOf(100))));
		}

		return new CartDTO(cart.getCartId(), cart.getUser().getUserId(), cart.getPromoCode(), cartItemDTOs, totalPrice);
	}
	
	//lấy tất cả giỏ hàng có trên hệ thống
	public List<CartDTO> getAllCart() {
		List<Cart> carts = cartRepository.findAll();
		return carts.stream().map(this::toCartDTO).collect(Collectors.toList());
	}

	// Lấy giỏ hàng theo userId
	public CartDTO getCart(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartRepository.save(newCart);
		});

		return toCartDTO(cart);
	}

	// Thêm món ăn vào giỏ hàng
	public CartDTO addToCart(Integer userId, Integer foodId, int quantity, String note) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseGet(() -> {
					Cart newCart = new Cart();
					newCart.setUser(userRepository.findById(userId).get());
					return cartRepository.save(newCart);
				});

		Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));

		// Áp dụng giảm giá 90% nếu isPromotion = true
		BigDecimal itemPrice = food.getIsPromotion() ? food.getPrice().multiply(BigDecimal.valueOf(0.9))
				: food.getPrice();

		Optional<CartItem> existingItem = cart.getCartItems().stream()
				.filter(item -> item.getFood().getFoodId().equals(foodId)).findFirst();

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			item.setQuantity(item.getQuantity() + quantity);
			item.setNote(note);
			item.setPrice(itemPrice); // Cập nhật giá với giảm giá
		} else {
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setFood(food);
			newItem.setQuantity(quantity);
			newItem.setNote(note);
			newItem.setPrice(itemPrice); // Áp dụng giá đã giảm
			cart.getCartItems().add(newItem);
		}

		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// Cập nhật số lượng món ăn
	public CartDTO updateCart(Integer userId, Integer foodId, int quantity) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));

		// Áp dụng giảm giá 90% nếu isPromotion = true
		BigDecimal itemPrice = food.getIsPromotion() ? food.getPrice().multiply(BigDecimal.valueOf(0.9))
				: food.getPrice();

		cart.getCartItems().forEach(item -> {
			if (item.getFood().getFoodId().equals(foodId)) {
				item.setQuantity(quantity);
				item.setPrice(itemPrice); // Cập nhật giá với giảm giá
			}
		});
		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// Xóa món ăn khỏi giỏ hàng
	public CartDTO removeFromCart(Integer userId, Integer foodId) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.getCartItems().removeIf(item -> item.getFood().getFoodId().equals(foodId));
		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// Áp dụng khuyến mãi
	public CartDTO applyPromotion(Integer userId, String promoCode) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		Promotion promotion = promotionRepository.findByCode(promoCode)
				.orElseThrow(() -> new IllegalArgumentException("Invalid promotion code"));

		if (!promotion.getIsActive()) {
			throw new IllegalStateException("Promotion is not active");
		}
		if (promotion.getEndDate().isBefore(LocalDate.now())) {
			throw new IllegalStateException("Promotion has expired");
		}
		if (promotion.getStartDate().isAfter(LocalDate.now())) {
			throw new IllegalStateException("Promotion is not yet valid");
		}

		cart.setPromoCode(promoCode);
		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// huỷ áp dụng khuyến mãi
	public CartDTO removePromotion(Integer userId) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.setPromoCode(null);
		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// sửa ghi chú cho món ăn trong giỏ hàng
	public CartDTO updateNoteInCart(Integer userId, Integer foodId, String note) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.getCartItems().forEach(item -> {
			if (item.getFood().getFoodId().equals(foodId)) {
				item.setNote(note);
			}
		});
		cartRepository.save(cart);
		return toCartDTO(cart);
	}

	// Xóa toàn bộ giỏ hàng
	public void clearCart(Integer userId) {
		Cart cart = cartRepository
				.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cart.getCartItems().clear();
		cartRepository.save(cart);
	}
}