package com.project.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.Entity.Food.Status;

public class FoodDTO {
    private Integer foodId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Status status;
    private Boolean isPromotion;
    private Integer orderCount;
    private LocalDateTime createdAt;
    private String categoryName;
    
	public FoodDTO(Integer foodId, String name, String description, BigDecimal price, String imageUrl, Status status,
			Boolean isPromotion, Integer orderCount, LocalDateTime createdAt, String categoryName) {
		super();
		this.foodId = foodId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.status = status;
		this.isPromotion = isPromotion;
		this.orderCount = orderCount;
		this.createdAt = createdAt;
		this.categoryName = categoryName;
	}
	
	public FoodDTO() {
		
	}
	public Integer getFoodId() {
		return foodId;
	}
	public void setFoodId(Integer foodId) {
		this.foodId = foodId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Boolean getIsPromotion() {
		return isPromotion;
	}
	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}
	public Integer getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

    
    
}
