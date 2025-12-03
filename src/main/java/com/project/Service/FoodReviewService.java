package com.project.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.Entity.Food;
import com.project.Entity.FoodReview;
import com.project.Entity.User;
import com.project.Repository.FoodRepository;
import com.project.Repository.FoodReviewRepository;
import com.project.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class FoodReviewService {
	@Autowired
	private FoodReviewRepository reviewRepo;

	@Autowired
	private FoodRepository foodRepo;

	@Autowired
	private UserRepository userRepo;

	// lấy tất cả đánh giá
	public Iterable<FoodReview> getAllReviews() {
		return reviewRepo.findAll();
	}

	// lấy đánh giá theo món ăn
	public Iterable<FoodReview> getReviewsByFoodId(Integer foodId) {
		return reviewRepo.findByFoodFoodIdAndIsVisibleTrue(foodId);
	}

	// lấy đánh giá theo người dùng
	public Iterable<FoodReview> getReviewsByUserId(Integer userId) {
		return reviewRepo.findByUserUserIdAndIsVisibleTrue(userId);
	}

	// cập nhật đánh giá và số lượng đánh giá
	@Transactional
	public void updateFoodRating(Integer foodId) {

		Long count = reviewRepo.countByFoodFoodId(foodId);

		Double avg = reviewRepo.findAverageRatingByFoodId(foodId);

		Food food = foodRepo.findById(foodId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn ID: " + foodId));

		food.setReviewCount(count.intValue());
		food.setAverageRating(avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0);

		foodRepo.save(food);
	}

	// viết đánh giá cho món ăn
	// Trong FoodReviewService.java
	public boolean writeReview(Integer foodId, Integer userId, Integer rating, String comment) {
		try {
			Food food = foodRepo.findById(foodId).orElseThrow(() -> new RuntimeException("Món ăn không tồn tại"));
			User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

			FoodReview review = new FoodReview();
			review.setFood(food);
			review.setUser(user);
			review.setRating(rating);
			review.setComment(comment);
			review.setCreatedAt(LocalDateTime.now());
			review.setIsVisible(true);

			reviewRepo.save(review);
			updateFoodRating(foodId);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// xoá đánh giá của bản thân
	public boolean deleteMyReview(Integer reviewId, Integer userId) {
		try {
			FoodReview review = reviewRepo.findById(reviewId)
					.orElseThrow(() -> new RuntimeException("Đánh giá không tồn tại"));

			if (!review.getUser().getUserId().equals(userId)) {
				throw new RuntimeException("Người dùng không có quyền xóa đánh giá này");
			}

			reviewRepo.delete(review);
			updateFoodRating(review.getFood().getFoodId());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Sửa đánh giá của bản thân
	public boolean editMyReview(Integer reviewId, Integer userId, Integer rating, String comment) {
		try {
			FoodReview review = reviewRepo.findById(reviewId)
					.orElseThrow(() -> new RuntimeException("Đánh giá không tồn tại"));

			if (!review.getUser().getUserId().equals(userId)) {
				throw new RuntimeException("Người dùng không có quyền sửa đánh giá này");
			}

			review.setRating(rating);
			review.setComment(comment);
			review.setCreatedAt(LocalDateTime.now());

			reviewRepo.save(review);
			updateFoodRating(review.getFood().getFoodId());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ẩn đánh giá
	public boolean hideReview(Integer reviewId) {
		try {
			reviewRepo.hideReviewById(reviewId);
			updateFoodRating(reviewRepo.findById(reviewId).get().getFood().getFoodId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// hiện đánh giấ
	public boolean showReview(Integer reviewId) {
		try {
			reviewRepo.showReviewById(reviewId);
			updateFoodRating(reviewRepo.findById(reviewId).get().getFood().getFoodId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
