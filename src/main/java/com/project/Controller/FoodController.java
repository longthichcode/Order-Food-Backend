package com.project.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.DTO.ErrDTO;
import com.project.DTO.FoodDTO;
import com.project.Entity.Food;
import com.project.Service.CategoryService;
import com.project.Service.FoodService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/foods")
@CrossOrigin(origins = "http://localhost:4200/")
public class FoodController {

	FoodService foodService;
	CategoryService categoryService;

	public FoodController(FoodService foodService, CategoryService categoryService) {
		this.foodService = foodService;
		this.categoryService = categoryService;
	}

	// Lấy tất cả món
	@GetMapping
	public ResponseEntity<List<Food>> getAllFoods() {
		List<Food> foods = foodService.getAllFoods();
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}

	// tìm món phổ biến nhất (theo số lượng đơn đã đặt)
	@GetMapping("/popular")
	public ResponseEntity<List<FoodDTO>> getTopPopularFoods(@RequestParam(defaultValue = "8") int limit) {
		List<FoodDTO> foods = foodService.getTopPopularFoods(limit);
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}

	// tìm món theo danh mục
	@GetMapping("/by-category")
	public ResponseEntity<List<FoodDTO>> getFoodsByCategory(@RequestParam int categoryId) {
		List<FoodDTO> foods = foodService.getFoodsByCategory(categoryId);
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}

	// tìm món theo tên (có thể chứa một phần tên)
	@GetMapping("/by-name")
	public ResponseEntity<List<FoodDTO>> getFoodsByName(@RequestParam String name) {
		List<FoodDTO> foods = foodService.getFoodsByName(name);
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}
	
	// tìm món theo trạng thái
	@GetMapping("/by-status")
	public ResponseEntity<List<FoodDTO>> getFoodsByStatus(@RequestParam String status) {
		List<FoodDTO> foods = foodService.getFoodsByStatus(status);
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}
	
	// tìm món theo khoảng giá
	@GetMapping("/by-price")
	public ResponseEntity<List<FoodDTO>> getFoodsByPrice(@RequestParam double minPrice, @RequestParam double maxPrice) {
		List<FoodDTO> foods = foodService.getFoodsByPriceRange(minPrice, maxPrice);
		return new ResponseEntity<>(foods, HttpStatus.OK);
	}

	// lấy món theo id
	@GetMapping("/by-id")
	public ResponseEntity<Food> getFoodById(@RequestParam int foodId) {
		Food food = foodService.getFoodById(foodId);
		if (food == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(food, HttpStatus.OK);
	}

	// chỉnh sửa món
	@PutMapping("/update")
	public ResponseEntity<?> updateFood(@RequestBody Food food, HttpServletRequest request) {
		try {
			Food existingFood = foodService.getFoodById(food.getFoodId());
			if (existingFood == null) {
				ErrDTO err = new ErrDTO("Món ăn không tồn tại", request.getRequestURI());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
			}
			existingFood.setName(food.getName());
			existingFood.setDescription(food.getDescription());
			existingFood.setPrice(food.getPrice());
			existingFood.setImageUrl(food.getImageUrl());
			existingFood.setStatus(food.getStatus());
			existingFood.setIsPromotion(food.getIsPromotion());
			existingFood.setCategory(categoryService.getCategoryById(food.getCategory().getCategoryId()));
			existingFood.setOrderCount(food.getOrderCount());
			existingFood.setCreatedAt(food.getCreatedAt());
			Food updatedFood = foodService.addFood(existingFood);

			System.out.println(food.toString());
			return ResponseEntity.ok(updatedFood);
		} catch (Exception e) {
			ErrDTO err = new ErrDTO("Không thể cập nhật món ăn: " + e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
		}
	}

	// Thêm món mới
	@PostMapping("/add")
	public ResponseEntity<?> addFood(@RequestBody Food foodDTO, HttpServletRequest request) {
		try {
			Food food = new Food();
			food.setName(foodDTO.getName());
			food.setDescription(foodDTO.getDescription());
			food.setPrice(foodDTO.getPrice());
			food.setImageUrl(foodDTO.getImageUrl());
			food.setStatus(foodDTO.getStatus());
			food.setOrderCount(0);
			food.setCreatedAt(java.time.LocalDateTime.now());
			food.setIsPromotion(foodDTO.getIsPromotion());
			food.setCategory(categoryService.getCategoryById(foodDTO.getCategory().getCategoryId()));
			Food savedFood = foodService.addFood(food);
			System.out.println(savedFood.toString());
			return ResponseEntity.status(HttpStatus.CREATED).body(savedFood);
		} catch (Exception e) {
			ErrDTO err = new ErrDTO("Không thể thêm món ăn: " + e.getMessage(), request.getRequestURI());
			System.out.println("vào đến đây");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
		}
	}

	// xoá món
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteFood(@RequestParam int foodId, HttpServletRequest request) {
		try {
			boolean isDeleted = foodService.deleteFood(foodId);
			if (isDeleted) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			} else {
				ErrDTO err = new ErrDTO("Món ăn không tồn tại", request.getRequestURI());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
			}
		} catch (Exception e) {
			ErrDTO err = new ErrDTO("Không thể xoá món ăn: " + e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
		}
	}
}
