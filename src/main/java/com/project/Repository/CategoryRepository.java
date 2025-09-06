package com.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
}