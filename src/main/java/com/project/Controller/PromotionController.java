package com.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Service.PromotionService;

@RestController
@RequestMapping("/promotions")
@CrossOrigin(origins = "http://localhost:4200/")
public class PromotionController {
	@Autowired private PromotionService promotionService;
	// Controller cho các endpoint liên quan đến khuyến mãi
	// lấy tất cả khuyến mãi
	@GetMapping
	public Object getAllPromotions() {
		return promotionService.getAllPromotions();
	}
	
	//lấy ra các khuyến mãi đang diễn ra
	@GetMapping("/current")
	public Object getCurrentPromotions() {
		return promotionService.getCurrentPromotions();
	}
}
