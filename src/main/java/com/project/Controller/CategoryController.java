package com.project.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.Service.CategoryService;
import com.project.Service.FoodService;

import jakarta.servlet.http.HttpServletRequest;

import com.project.DTO.ErrDTO;
import com.project.Entity.Category;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:4200/")
public class CategoryController {

	FoodService foodService;
	CategoryService categoryService;

	public CategoryController(FoodService foodService, CategoryService categoryService) {
		this.foodService = foodService;
		this.categoryService = categoryService;
	}

	// lấy tất cả danh mục
	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
	public Iterable<Category> getAllCategory() {
		return categoryService.getAllCategory();
	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
		try {
			// Kiểm tra tên danh mục trùng
			Category existingCategory = categoryService.getCategoryByName(category.getCategoryName());
			if (existingCategory != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrDTO("Category name already exists", request.getServletPath()));
			}

			// Thêm danh mục mới
			Category createdCategory = categoryService.addCategory(category);
			if (createdCategory == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ErrDTO("Failed to create category", request.getServletPath()));
			}

			return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrDTO(e.getMessage(), request.getServletPath()));
		}
	}

	@DeleteMapping("/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteCategory(@RequestParam Integer id, HttpServletRequest request) {
		try {
			Category existingCategory = categoryService.getCategoryById(id);
			System.out.println(id);
			if (existingCategory == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ErrDTO("Category not found", request.getServletPath()));
			}

			categoryService.deleteCategory(id);

			// Trả về JSON
			Map<String, Object> response = new HashMap<>();
			response.put("status", 200);
			response.put("message", "Category deleted successfully");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrDTO(e.getMessage(), request.getServletPath()));
		}
	}
	
	@PutMapping("/update")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateCategory(@RequestBody Category category, HttpServletRequest request) {
		try {
			Category existingCategory = categoryService.getCategoryById(category.getCategoryId());
			if (existingCategory == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ErrDTO("Category not found", request.getServletPath()));
			}

			// Kiểm tra tên danh mục trùng
			Category categoryWithSameName = categoryService.getCategoryByName(category.getCategoryName());
			if (categoryWithSameName != null && !categoryWithSameName.getCategoryId().equals(category.getCategoryId())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrDTO("Category name already exists", request.getServletPath()));
			}

			existingCategory.setCategoryName(category.getCategoryName());
			existingCategory.setDescription(category.getDescription());
			existingCategory.setIsActive(category.getIsActive());
			Category updatedCategory = categoryService.updateCategory(existingCategory);

			return ResponseEntity.ok(updatedCategory);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrDTO(e.getMessage(), request.getServletPath()));
		}
	}

}