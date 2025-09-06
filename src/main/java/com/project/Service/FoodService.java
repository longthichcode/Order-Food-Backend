package com.project.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.project.DTO.FoodDTO;
import com.project.Entity.Food;
import com.project.Repository.FoodRepository;

@Service
public class FoodService {
	FoodRepository foodRepository;

	public FoodService(FoodRepository foodRepository) {
		this.foodRepository = foodRepository;
	}

	// Lấy tất cả món
	public List<Food> getAllFoods() {
		return foodRepository.findAll();
	}

	// tìm món phổ biến nhất (theo số lượng đơn đã đặt)
	public List<FoodDTO> getTopPopularFoods(int limit) {
		List<Food> f = foodRepository.findTopPopularFoods(Pageable.ofSize(limit));
		List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
		for (Food food : f) {
			foodDTOs.add(mapToDTO(food));
		}
		return foodDTOs;
	}

	// tìm món theo danh mục
	public List<FoodDTO> getFoodsByCategory(int categoryId) {
		List<Food> f = foodRepository.findByCategoryId(categoryId);
		List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
		for (Food food : f) {
			foodDTOs.add(mapToDTO(food));
		}
		return foodDTOs;
	}

	// tìm món theo tên (có thể chứa một phần tên)
	public List<FoodDTO> getFoodsByName(String name) {
		List<Food> f = foodRepository.findByNameContainingIgnoreCase(name);
		List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
		for (Food food : f) {
			foodDTOs.add(mapToDTO(food));
		}
		return foodDTOs;

	}

	// tìm món theo trạng thái
	public List<FoodDTO> getFoodsByStatus(String status) {
		List<Food> f = foodRepository.findByStatus(status);
		List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
		for (Food food : f) {
			foodDTOs.add(mapToDTO(food));
		}
		return foodDTOs;
	}

	// tìm kiếm theo khoảng giá
	public List<FoodDTO> getFoodsByPriceRange(double minPrice, double maxPrice) {
		
		BigDecimal min = BigDecimal.valueOf(minPrice);
		BigDecimal max = BigDecimal.valueOf(maxPrice);
		
		List<Food> f = foodRepository.findByPriceBetween(min, max);
		List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
		for (Food food : f) {
			foodDTOs.add(mapToDTO(food));
		}
		return foodDTOs;
	}

	// thêm món mới
	public Food addFood(Food food) {
		return foodRepository.save(food);
	}

	// lấy món theo ID
	public Food getFoodById(int foodId) {
		Food food = foodRepository.findById(foodId).orElse(null);
		if (food != null) {
			return food;
		} else {
			return null;
		}
	}

	// cập nhật món
	public Food updateFood(Food food) {
		return foodRepository.save(food);
	}

	// Xoá món theo ID
	public boolean deleteFood(int foodId) {
		Food existingFood = foodRepository.findById(foodId).orElse(null);
		if (existingFood != null) {
			foodRepository.deleteById(foodId);
			return true;
		} else {
			return false;
		}
	}

	// map to DTO
	private FoodDTO mapToDTO(Food food) {
		FoodDTO foodDTO = new FoodDTO();
		foodDTO.setFoodId(food.getFoodId());
		foodDTO.setName(food.getName());
		foodDTO.setDescription(food.getDescription());
		foodDTO.setPrice(food.getPrice());
		foodDTO.setImageUrl(food.getImageUrl());
		foodDTO.setStatus(food.getStatus());
		foodDTO.setIsPromotion(food.getIsPromotion());
		foodDTO.setOrderCount(food.getOrderCount());
		foodDTO.setCreatedAt(food.getCreatedAt());
		if (food.getCategory() != null) {
			foodDTO.setCategoryName(food.getCategory().getCategoryName());
		} else {
			foodDTO.setCategoryName(null);
		}
		return foodDTO;
	}
}
