package com.project.Controller;

import com.project.Entity.FoodReview;
import com.project.Service.FoodReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:4200/")
public class FoodReviewController {

	@Autowired
	private FoodReviewService reviewService;

	// 1. Lấy tất cả đánh giá
	@GetMapping
	public ResponseEntity<Iterable<FoodReview>> getAllReviews() {
		return ResponseEntity.ok(reviewService.getAllReviews());
	}

	// 2. Lấy đánh giá theo món ăn (hiển thị dưới món ăn)
	@GetMapping("/food/{foodId}")
	public ResponseEntity<Iterable<FoodReview>> getReviewsByFoodId(@PathVariable Integer foodId) {
		return ResponseEntity.ok(reviewService.getReviewsByFoodId(foodId));
	}

	// 3. Lấy đánh giá của người dùng hiện tại (xem lịch sử đánh giá)
	@GetMapping("/user/{userId}")
	public ResponseEntity<Iterable<FoodReview>> getReviewsByUserId(@PathVariable Integer userId) {
		return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
	}

	// 4. Viết đánh giá mới (khách hàng)
	@PostMapping("/food/{foodId}/user/{userId}")
	public ResponseEntity<String> writeReview(@PathVariable Integer foodId, @PathVariable Integer userId,
			@RequestBody ReviewRequest request) {

		boolean success = reviewService.writeReview(foodId, userId, request.getRating(), request.getComment());

		return success ? new ResponseEntity<>(request.getComment(), HttpStatus.OK)
				: ResponseEntity.badRequest().body("Đánh giá thất bại!");
	}

	// 5. Admin ẩn đánh giá (ẩn khỏi hiển thị)
	@PutMapping("/hide/{reviewId}")
	public ResponseEntity<String> hideReview(@PathVariable Integer reviewId) {
		boolean success = reviewService.hideReview(reviewId);
		if (success) {
			return ResponseEntity.ok("Đã ẩn đánh giá!");
		} else {
			return ResponseEntity.badRequest().body("Ẩn thất bại!");
		}
	}

	// 6. Admin hiện lại đánh giá
	@PutMapping("/show/{reviewId}")
	public ResponseEntity<String> showReview(@PathVariable Integer reviewId) {
		boolean success = reviewService.showReview(reviewId);
		if (success) {
			return ResponseEntity.ok("Đã hiện đánh giá!");
		} else {
			return ResponseEntity.badRequest().body("Hiện thất bại!");
		}
	}

	// 7. Cập nhật đánh giá món ăn (chức năng bổ sung)
	@PutMapping("/{reviewId}/user/{userId}")
	public ResponseEntity<String> updateReview(@PathVariable Integer reviewId, @PathVariable Integer userId,
			@RequestBody ReviewRequest request) {
		boolean success = reviewService.editMyReview(reviewId, userId, request.getRating(), request.getComment());
		if (success) {
			return ResponseEntity.ok("Đã cập nhật đánh giá!");
		} else {
			return ResponseEntity.badRequest().body("Cập nhật thất bại!");
		}
	}

	// 8. Xoá đánh giá món ăn (chức năng bổ sung)
	@DeleteMapping("/{reviewId}/user/{userId}")
	public ResponseEntity<String> deleteMyReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
		boolean success = reviewService.deleteMyReview(reviewId, userId);
		if (success) {
			return ResponseEntity.ok("Đã xoá đánh giá!");
		} else {
			return ResponseEntity.badRequest().body("Xoá thất bại!");
		}
	}
}

// DTO
class ReviewRequest {
	private Integer rating;
	private String comment;

	// Getters and Setters
	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}