package com.project.Service;

import org.springframework.stereotype.Service;

import com.project.Entity.Category;
import com.project.Repository.CategoryRepository;

@Service
public class CategoryService {
	CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		super();
		this.categoryRepository = categoryRepository;
	}
	// lấy tất cả danh mục 
	public Iterable<Category> getAllCategory() {
		return categoryRepository.findAll();
	}
	
	//lấy theo id
	public Category getCategoryById(int id) {
		return categoryRepository.findById(id).orElse(null);
	}
}
