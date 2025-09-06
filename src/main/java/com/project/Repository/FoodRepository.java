package com.project.Repository;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.project.Entity.Food;

public interface FoodRepository extends JpaRepository<Food, Integer>, JpaSpecificationExecutor<Food> {

    // Tìm món phổ biến nhất (theo số lượng đơn đã đặt)
    @Query("SELECT f FROM Food f WHERE f.status = 'AVAILABLE' ORDER BY f.orderCount DESC")
    List<Food> findTopPopularFoods(Pageable pageable);

    // Tìm món theo danh mục
    @Query("SELECT f FROM Food f JOIN f.category c WHERE f.status = 'AVAILABLE' AND c.categoryId = ?1")
    List<Food> findByCategoryId(int categoryId);

    // Tìm món theo tên (có thể chứa một phần tên)
    @Query("SELECT f FROM Food f WHERE f.status = 'AVAILABLE' AND LOWER(f.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Food> findByNameContainingIgnoreCase(String name);

    // Tìm món theo trạng thái
    @Query("SELECT f FROM Food f WHERE f.status = ?1")
    List<Food> findByStatus(String status);

    // Tìm món theo khoảng giá
    @Query("SELECT f FROM Food f WHERE f.status = 'AVAILABLE' AND f.price BETWEEN ?1 AND ?2")
    List<Food> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Tìm món theo khuyến mãi
    @Query("SELECT f FROM Food f WHERE f.status = 'AVAILABLE' AND f.isPromotion = ?1")
    List<Food> findByIsPromotion(boolean isPromotion);
    
}
