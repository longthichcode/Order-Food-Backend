package com.project.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Service.CategoryService;
import com.project.Service.FoodService;


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
	
	//lấy tất cả danh mục 
	@GetMapping
	public Iterable<com.project.Entity.Category> getAllCategory() {
		return categoryService.getAllCategory();
	}

}