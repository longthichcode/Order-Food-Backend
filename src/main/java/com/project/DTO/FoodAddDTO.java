package com.project.DTO;

import java.math.BigDecimal;

import com.project.Entity.Food.Status;

public class FoodAddDTO {
	private int foodId;
	private String name;
	private String description;
	private BigDecimal price;
	private int categoryId;
	private String imageUrl;
	private Status status;
	private boolean isPromotion;
	public FoodAddDTO(int foodId, String name, String description, BigDecimal price, int categoryId, String imageUrl,
			Status status, boolean isPromotion) {
		super();
		this.foodId = foodId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.categoryId = categoryId;
		this.imageUrl = imageUrl;
		this.status = status;
		this.isPromotion = isPromotion;
	}
	
	public FoodAddDTO() {
		
	}

	public int getFoodId() {
		return foodId;
	}

	public void setFoodId(int foodId) {
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

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
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

	public boolean isPromotion() {
		return isPromotion;
	}

	public void setPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	@Override
	public String toString() {
		return "FoodAddDTO [foodId=" + foodId + ", name=" + name + ", description=" + description + ", price=" + price
				+ ", categoryId=" + categoryId + ", imageUrl=" + imageUrl + ", status=" + status + ", isPromotion="
				+ isPromotion + "]";
	}
	
	
	
}