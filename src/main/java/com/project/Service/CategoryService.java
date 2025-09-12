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

	// lấy theo id
	public Category getCategoryById(Integer id) {
		return categoryRepository.findById(id).orElse(null);
	}

	// lấy theo tên
	public Category getCategoryByName(String name) {
		return categoryRepository.findAll().stream().filter(c -> c.getCategoryName().equals(name)).findFirst()
				.orElse(null);
	}
	
	//sửa danh mục
	public Category updateCategory(Category category) {
		return categoryRepository.save(category);
	}

	// thêm danh mục
	public Category addCategory(Category category) {
		return categoryRepository.save(category);
	}
	
	// xóa danh mục
	public void deleteCategory(Integer id) {
		categoryRepository.deleteById(id);
	}
}
