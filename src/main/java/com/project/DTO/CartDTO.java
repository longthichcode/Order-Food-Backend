package com.project.DTO;

import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    private Integer cartId;
    private Integer userId;
    private String promoCode;
    private List<CartItemDTO> cartItems;
    private BigDecimal totalPrice;

    public CartDTO(Integer cartId, Integer userId, String promoCode, List<CartItemDTO> cartItems, BigDecimal totalPrice) {
        this.cartId = cartId;
        this.userId = userId;
        this.promoCode = promoCode;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
    }

    // Getters và setters
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}