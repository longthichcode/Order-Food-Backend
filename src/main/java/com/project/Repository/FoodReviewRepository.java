package com.project.Repository;

import com.project.Entity.FoodReview;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FoodReviewRepository extends JpaRepository<FoodReview, Integer> {
	// Đếm số đánh giá hiển thị cho một món ăn cụ thể
    Long countByFoodFoodId(Integer foodId);

    // Tính điểm đánh giá trung bình cho một món ăn cụ thể
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM FoodReview r WHERE r.food.foodId = :foodId AND r.isVisible = true")
    Double findAverageRatingByFoodId(@Param("foodId") Integer foodId);

    // Lấy tất cả đánh giá hiển thị cho một món ăn cụ thể
    List<FoodReview> findByFoodFoodIdAndIsVisibleTrue(Integer foodId);

    // Lấy tất cả đánh giá hiển thị của một người dùng cụ thể
    List<FoodReview> findByUserUserIdAndIsVisibleTrue(Integer userId);
    
    //ẩn đánh giá món ăn 
    @Transactional
    @Modifying
    @Query("UPDATE FoodReview r SET r.isVisible = false WHERE r.reviewId = :reviewId")
    void hideReviewById(@Param("reviewId") Integer reviewId);
    
    //hiện đánh giá món ăn 
    @Transactional
    @Modifying
    @Query("UPDATE FoodReview r SET r.isVisible = true WHERE r.reviewId = :reviewId")
    void showReviewById(@Param("reviewId") Integer reviewId);
}