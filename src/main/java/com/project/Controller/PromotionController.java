package com.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.Entity.Promotion;
import com.project.Service.PromotionService;

@RestController
@RequestMapping("/promotions")
@CrossOrigin(origins = "http://localhost:4200/")
public class PromotionController {
	@Autowired
	private PromotionService promotionService;

	// lấy tất cả khuyến mãi
	@GetMapping
	public Object getAllPromotions() {
		return promotionService.getAllPromotions();
	}

	// lấy ra các khuyến mãi đang diễn ra
	@GetMapping("/current")
	public Object getCurrentPromotions() {
		return promotionService.getCurrentPromotions();
	}

	// lấy khuyến mãi theo Id
	@GetMapping("/byId")
	public ResponseEntity<?> getPromotionById(@RequestBody Integer promotionId) {
		try {
			Promotion promotion = promotionService.getPromotionById(promotionId);
			return ResponseEntity.ok(promotion);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// cập nhật khuyến mãi
	@PutMapping("/update")
	public ResponseEntity<?> updatePromotion(@RequestBody Promotion promotion) {
		try {
			Promotion updatedPromotion = promotionService.updatePromotion(promotion);
			return ResponseEntity.ok(updatedPromotion);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// xoá khuyến mãi
	@DeleteMapping("/delete/{promotionId}")
	public ResponseEntity<?> deletePromotion(@PathVariable Integer promotionId) {
		try {
			promotionService.deletePromotion(promotionId);
			return ResponseEntity.ok(promotionId);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	//thêm khuyến mãi
	@PostMapping("/add")
	public ResponseEntity<?> addPromotion(@RequestBody Promotion promotion) {
		try {
			Promotion newPromotion = promotionService.addPromotion(promotion);
			return ResponseEntity.ok(newPromotion);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
