package com.project.Entity;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false, length = 100)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    public Category(Integer categoryId, String categoryName, String description, Boolean isActive) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.description = description;
		this.isActive = isActive;
	}

    public Category() {
    	
    }
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + ", description=" + description
				+ ", isActive=" + isActive + "]";
	}
	
	
	
}