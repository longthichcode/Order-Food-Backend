package com.project.DTO;

import java.math.BigDecimal;

public class CartItemDTO {
    private Integer cartItemId;
    private Integer foodId;
    private String foodName;
    private String foodImage;
    private Integer quantity;
    private BigDecimal price;
    private String note;
	public CartItemDTO(Integer cartItemId, Integer foodId, String foodName, String foodImage, Integer quantity,
			BigDecimal price, String note) {
		super();
		this.cartItemId = cartItemId;
		this.foodId = foodId;
		this.foodName = foodName;
		this.foodImage = foodImage;
		this.quantity = quantity;
		this.price = price;
		this.note = note;
	}
	public CartItemDTO() {}
	public Integer getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(Integer cartItemId) {
		this.cartItemId = cartItemId;
	}
	public Integer getFoodId() {
		return foodId;
	}
	public void setFoodId(Integer foodId) {
		this.foodId = foodId;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public String getFoodImage() {
		return foodImage;
	}
	public void setFoodImage(String foodImage) {
		this.foodImage = foodImage;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

    
}