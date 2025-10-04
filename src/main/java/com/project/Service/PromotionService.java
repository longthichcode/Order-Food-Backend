package com.project.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.Entity.Promotion;
import com.project.Repository.PromotionRepository;

public class PromotionService {
	@Autowired
	private PromotionRepository promotionRepository;

	// ;lấy tất cả khuyến mãi
	public List<Promotion> getAllPromotions() {
		return promotionRepository.findAll();
	}

	// lấy theo Id
	public Promotion getPromotionById(Integer promotionId) {
		return promotionRepository.findById(promotionId).orElseThrow(null);
	}
	
	//lấy khuyến mãi theo khoảng thời gian 
	public List<Promotion> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate) {
		// Giả sử bạn đã có phương thức trong repository để tìm khuyến mãi theo khoảng thời gian
		return promotionRepository.findByStartDateBetween(startDate, endDate);
	}
	
	// xoá khuyến mãi
	public void deletePromotion(Integer promotionId) {
		if (!promotionRepository.existsById(promotionId)) {
			throw new RuntimeException("Promotion not found");
		}
		promotionRepository.deleteById(promotionId);
	}
	
	//
}
